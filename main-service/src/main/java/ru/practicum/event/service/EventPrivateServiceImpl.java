package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.*;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestParticipationMapper;
import ru.practicum.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.EventRequestStatusUpdateResult;
import ru.practicum.request.model.RequestParticipation;
import ru.practicum.request.repository.RequestParticipationRepository;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.event.model.EventState.PUBLISHED;
import static ru.practicum.event.model.EventStateAction.CANCEL_REVIEW;
import static ru.practicum.event.model.EventStateAction.SEND_TO_REVIEW;
import static ru.practicum.request.model.RequestParticipationState.*;
import static ru.practicum.utils.Constants.NUMBER_HOURS_BEFORE_EVENT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final RequestParticipationRepository requestParticipationRepository;
    private final RequestParticipationMapper requestParticipationMapper;
    private final EventStatisticsService eventStatisticsService;

    @Override
    public List<EventShortDto> getEventShort(Long userId, PageRequest pageRequest) {
        List<Event> foundEvents = eventRepository.findAllByInitiator_Id(userId, pageRequest);

        Map<Long, Integer> views = eventStatisticsService.getViews(foundEvents);
        Map<Long, Integer> confirmedRequests = eventStatisticsService.getConfirmedRequests(foundEvents);

        List<EventShortDto> resultList = new ArrayList<>();
        for (Event foundEvent : foundEvents) {
            resultList.add(eventMapper.toEventShortDto(foundEvent,
                    confirmedRequests.getOrDefault(foundEvent.getId(), 0),
                    views.getOrDefault(foundEvent.getId(), 0)));
        }

        return resultList;
    }

    @Transactional
    @Override
    public EventFullDto save(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(NUMBER_HOURS_BEFORE_EVENT))) {
            throw new ForbiddenException("Дата и время на которые намечено событие не может " +
                    "быть раньше, чем через два часа от текущего момента. Value eventDate: " + newEventDto.getEventDate());
        }

        if (newEventDto.getParticipantLimit() < 0) {
            throw new BadRequestException("Значение поля participantLimit=" + newEventDto.getParticipantLimit() +
                    " не может быть меньше нуля");
        }

        User initiator = userService.findById(userId);
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id=" + newEventDto.getCategory() + " не найдена"));

        Event event = eventMapper.toEvent(newEventDto, category, initiator, newEventDto.getLocation());
        event.setState(EventState.PENDING);

        Event saveEvent = eventRepository.save(event);

        // у нового события поля confirmedRequests и views = 0
        return eventMapper.toEventFullDto(saveEvent, categoryMapper.toCategoryDto(category), 0,
                userMapper.toUserShortDto(initiator), 0);
    }

    @Override
    public EventFullDto getEventFull(Long userId, Long eventId) {
        Event event = findById(eventId);

        if (!Objects.equals(userId, event.getInitiator().getId())) {
            throw new ForbiddenException("Вы не являетесь владельцем мероприятия");
        }

        Map<Long, Integer> views = eventStatisticsService.getViews(List.of(event));
        Map<Long, Integer> confirmedRequests = eventStatisticsService.getConfirmedRequests(List.of(event));

        return eventMapper.toEventFullDto(
                event,
                categoryMapper.toCategoryDto(event.getCategory()),
                confirmedRequests.getOrDefault(event.getId(), 0),
                userMapper.toUserShortDto(event.getInitiator()),
                views.getOrDefault(event.getId(), 0));
    }

    @Transactional
    @Override
    public EventFullDto patchUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = findById(eventId);

        if (!Objects.equals(userId, event.getInitiator().getId())) {
            throw new ForbiddenException("Вы не являетесь владельцем мероприятия");
        }

        if (event.getState() == PUBLISHED) {
            throw new ConflictException("Только pending или canceled события могут быть изменены");
        }

        // update event in DB
        Event updateEvent = eventRepository.save(checkBeforeUpdate(event, updateEventUserRequest));

        Map<Long, Integer> views = eventStatisticsService.getViews(List.of(updateEvent));
        Map<Long, Integer> confirmedRequests = eventStatisticsService.getConfirmedRequests(List.of(updateEvent));

        return eventMapper.toEventFullDto(
                updateEvent,
                categoryMapper.toCategoryDto(updateEvent.getCategory()),
                confirmedRequests.getOrDefault(updateEvent.getId(), 0),
                userMapper.toUserShortDto(updateEvent.getInitiator()),
                views.getOrDefault(updateEvent.getId(), 0));
    }

    private Event checkBeforeUpdate(Event event, UpdateEventUserRequest updateEventUserRequest) {
        String annotation = updateEventUserRequest.getAnnotation();
        if (annotation != null) {
            if (annotation.length() < 20 || annotation.length() > 2000 || annotation.isBlank()) {
                throw new BadRequestException("Длина поля annotation должна быть: min = 20, max = 2000");
            }
            event.setAnnotation(annotation);
        }

        Long catId = updateEventUserRequest.getCategory();
        if (catId != null) {
            if (catId < 1) {
                throw new BadRequestException("Поле category должно быть положительным");
            }
            Category category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new NotFoundException("Категория с id=" + catId + " не найдена"));
            event.setCategory(category);
        }

        String description = updateEventUserRequest.getDescription();
        if (description != null) {
            if (description.length() < 20 || description.length() > 7000 || description.isBlank()) {
                throw new BadRequestException("Длина поля description должна быть: min = 20, max = 7000");
            }
            event.setDescription(description);
        }

        LocalDateTime eventDate = updateEventUserRequest.getEventDate();
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(NUMBER_HOURS_BEFORE_EVENT))) {
                throw new BadRequestException("Дата и время на которые намечено событие не может " +
                        "быть раньше, чем через два часа от текущего момента. Value eventDate: " + eventDate);
            }
            event.setEventDate(eventDate);
        }

        Location location = updateEventUserRequest.getLocation();
        if (location != null) {
            event.setLat(location.getLat());
            event.setLon(location.getLon());
        }

        Boolean paid = updateEventUserRequest.getPaid();
        if (paid != null) {
            event.setPaid(paid);
        }

        Integer participantLimit = updateEventUserRequest.getParticipantLimit();
        if (participantLimit != null) {
            if (participantLimit < 0) {
                throw new BadRequestException("Значение поля participantLimit=" + participantLimit +
                        " не может быть меньше нуля");
            }
            event.setParticipantLimit(participantLimit);
        }

        Boolean requestModeration = updateEventUserRequest.getRequestModeration();
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }

        // проверка на event.getState() == EventState.PUBLISHED выполнена ранее
        // сейчас event находится в одном из двух состояний: PENDING или CANCELED
        EventStateAction stateAction = updateEventUserRequest.getStateAction();
        if (stateAction != null) {
            if (event.getState() == EventState.PENDING && stateAction == CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            }
            if (event.getState() == EventState.CANCELED && stateAction == SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            }
        }

        String title = updateEventUserRequest.getTitle();
        if (title != null) {
            if (title.length() < 3 || title.length() > 120 || title.isBlank()) {
                throw new BadRequestException("Длина поля title должна быть: min = 3, max = 120");
            }
            event.setTitle(title);
        }

        return event;
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequest(Long userId, Long eventId) {
        Event event = findById(eventId);

        if (!Objects.equals(userId, event.getInitiator().getId())) {
            throw new ForbiddenException("Вы не являетесь владельцем события");
        }

        return requestParticipationMapper.toParticipationRequestDto(
                requestParticipationRepository.findAllByEvent_Id(eventId));
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult patchUpdateStatus(Long userId, Long eventId,
                                                            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = findById(eventId);

        if (!Objects.equals(userId, event.getInitiator().getId())) {
            throw new ForbiddenException("Вы не являетесь владельцем события");
        }

        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        if (requestIds == null || requestIds.isEmpty()) {
            throw new BadRequestException("Не указаны идентификаторы запросов на участие");
        }

        // заявки на участие
        List<RequestParticipation> requests = requestParticipationRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());

        requests.stream()
                .filter(e -> e.getStatus() != PENDING)
                .findFirst()
                .ifPresent(e -> {
                    throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
                });

        Map<Long, Integer> eventIdToTotalRequests = eventStatisticsService.getConfirmedRequests(List.of(event));
        long requestConfirmedCount = eventIdToTotalRequests.getOrDefault(event.getId(), 0);
        if (event.getParticipantLimit() != 0) { // значит установлен лимит
            if (requestConfirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Достигнут лимит запросов на участие");
            }
        }

        if (event.getParticipantLimit() == 0 && !event.getRequestModeration()) {
            requests = requests.stream()
                    .peek(e -> e.setStatus(CONFIRMED))
                    .collect(Collectors.toList());

            requestParticipationRepository.saveAll(requests);

            return new EventRequestStatusUpdateResult(requestParticipationMapper.toParticipationRequestDto(requests),
                    new ArrayList<>());
        }

        List<RequestParticipation> confirmed = new ArrayList<>();
        List<RequestParticipation> rejected = new ArrayList<>();

        if (eventRequestStatusUpdateRequest.getStatus() == REJECTED) {
            rejected = requests.stream()
                    .peek(e -> e.setStatus(REJECTED))
                    .collect(Collectors.toList());

            requestParticipationRepository.saveAll(rejected);
        }

        if (eventRequestStatusUpdateRequest.getStatus() == CONFIRMED) {
            confirmed = requests.stream()
                    .limit(event.getParticipantLimit() - requestConfirmedCount)
                    .peek(e -> e.setStatus(CONFIRMED))
                    .collect(Collectors.toList());

            rejected = requests.stream()
                    .skip(event.getParticipantLimit() - requestConfirmedCount)
                    .peek(e -> e.setStatus(REJECTED))
                    .collect(Collectors.toList());

            requestParticipationRepository.saveAll(confirmed);
            requestParticipationRepository.saveAll(rejected);
        }

        return new EventRequestStatusUpdateResult(
                requestParticipationMapper.toParticipationRequestDto(confirmed),
                requestParticipationMapper.toParticipationRequestDto(rejected));
    }

    @Override
    public Event findById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
    }
}