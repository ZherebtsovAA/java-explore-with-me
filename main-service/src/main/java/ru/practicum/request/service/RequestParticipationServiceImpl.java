package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventPrivateService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestParticipationMapper;
import ru.practicum.request.model.RequestParticipation;
import ru.practicum.request.model.RequestParticipationState;
import ru.practicum.request.repository.RequestParticipationRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestParticipationServiceImpl implements RequestParticipationService {
    private final RequestParticipationRepository requestParticipationRepository;
    private final RequestParticipationMapper requestParticipationMapper;
    private final EventPrivateService eventPrivateService;
    private final UserService userService;

    @Override
    public List<ParticipationRequestDto> getRequestParticipationCurrentUser(Long userId) {
        User requester = userService.findById(userId);

        // инициатор события не может добавить запрос на участие в своём событии
        // поэтому можно просто выбрать все заяки от пользователя
        RequestParticipation templateSearchRequestParticipation = new RequestParticipation();
        templateSearchRequestParticipation.setRequester(requester);

        List<RequestParticipation> result = requestParticipationRepository.findAll(Example.of(templateSearchRequestParticipation));

        return requestParticipationMapper.toParticipationRequestDto(result);
    }

    @Transactional
    @Override
    public ParticipationRequestDto save(Long userId, Long eventId) {
        User requester = userService.findById(userId);
        Event event = eventPrivateService.findById(eventId);

        if (event.getInitiator().equals(requester)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        RequestParticipation templateSearchRequestParticipation = new RequestParticipation();
        Event templateSearchEvent = new Event();
        templateSearchEvent.setId(event.getId());
        templateSearchRequestParticipation.setEvent(templateSearchEvent);
        templateSearchRequestParticipation.setStatus(RequestParticipationState.CONFIRMED);

        RequestParticipation newRequestParticipation = requestParticipationMapper.toRequestParticipation(requester, event);

        templateSearchRequestParticipation.setStatus(null);
        templateSearchRequestParticipation.setRequester(requester);
        if (requestParticipationRepository.exists(Example.of(templateSearchRequestParticipation))) {
            throw new ConflictException("Нельзя добавить повторный запрос");
        }

        if (event.getRequestModeration()) { // требуется пре-модерация запросов на участие
            newRequestParticipation.setStatus(RequestParticipationState.PENDING);
        } else {
            newRequestParticipation.setStatus(RequestParticipationState.CONFIRMED);
        }

        if (event.getParticipantLimit() != 0) { // значит установлен лимит
            long requestCount = requestParticipationRepository.count(Example.of(templateSearchRequestParticipation));
            if (requestCount >= event.getParticipantLimit()) {
                throw new ConflictException("Достигнут лимит запросов на участие");
            }
        }

        if (event.getParticipantLimit() == 0) {
            newRequestParticipation.setStatus(RequestParticipationState.CONFIRMED);
        }

        return requestParticipationMapper.toParticipationRequestDto(requestParticipationRepository.save(newRequestParticipation));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelYourParticipationRequest(Long userId, Long requestId) {
        User user = userService.findById(userId);

        RequestParticipation requestParticipation = requestParticipationRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса на участие с id=" + userId + " не найдено"));

        if (!Objects.equals(user, requestParticipation.getRequester())) {
            throw new ForbiddenException("Вы не являетесь владельцем запроса на участие");
        }

        requestParticipation.setStatus(RequestParticipationState.CANCELED);

        return requestParticipationMapper.toParticipationRequestDto(requestParticipationRepository.save(requestParticipation));
    }
}