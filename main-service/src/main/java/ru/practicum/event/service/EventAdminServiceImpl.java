package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.*;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.EventSpecifications;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.practicum.event.model.EventState.*;
import static ru.practicum.event.model.EventStateAction.PUBLISH_EVENT;
import static ru.practicum.event.model.EventStateAction.REJECT_EVENT;
import static ru.practicum.utils.Constants.NUMBER_HOURS_FROM_DATE_PUBLICATION;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventStatisticsService eventStatisticsService;

    @Override
    public List<EventFullDto> getEventFull(List<Long> users, List<EventState> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest) {

        Specification<Event> searchCriteria = Specification
                .where(users == null || users.isEmpty() ? null : EventSpecifications.belongsToInitiators(users))
                .and(states == null || states.isEmpty() ? null : EventSpecifications.belongsToStates(states))
                .and(categories == null || categories.isEmpty() ? null : EventSpecifications.belongsToCategories(categories))
                .and(rangeStart == null ? null : EventSpecifications.eventDateGreaterThan(rangeStart))
                .and(rangeEnd == null ? null : EventSpecifications.eventDatelessThan(rangeEnd));

        List<Event> foundEvents = eventRepository.findAll(searchCriteria, pageRequest).getContent();
        Map<Long, Integer> views = eventStatisticsService.getViews(foundEvents);
        Map<Long, Integer> confirmedRequests = eventStatisticsService.getConfirmedRequests(foundEvents);

        List<EventFullDto> result = new ArrayList<>(foundEvents.size());
        for (Event event : foundEvents) {
            result.add(eventMapper.toEventFullDto(
                    event,
                    categoryMapper.toCategoryDto(event.getCategory()),
                    confirmedRequests.getOrDefault(event.getId(), 0),
                    userMapper.toUserShortDto(event.getInitiator()),
                    views.getOrDefault(event.getId(), 0),
                    eventMapper.toAdminCommentDto(event.getAdminComments()))
            );
        }

        return result;
    }

    @Transactional
    @Override
    public EventFullDto patchUpdateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        Event eventUpdate = eventRepository.save(updateAndCheckEvent(event, updateEventAdminRequest));

        Map<Long, Integer> views = eventStatisticsService.getViews(List.of(eventUpdate));
        Map<Long, Integer> confirmedRequests = eventStatisticsService.getConfirmedRequests(List.of(eventUpdate));

        return eventMapper.toEventFullDto(
                eventUpdate,
                categoryMapper.toCategoryDto(eventUpdate.getCategory()),
                confirmedRequests.getOrDefault(eventUpdate.getId(), 0),
                userMapper.toUserShortDto(eventUpdate.getInitiator()),
                views.getOrDefault(eventUpdate.getId(), 0),
                eventMapper.toAdminCommentDto(event.getAdminComments()));
    }

    private Event updateAndCheckEvent(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        String annotation = updateEventAdminRequest.getAnnotation();
        if (annotation != null) {
            if (annotation.length() < 20 || annotation.length() > 2000 || annotation.isBlank()) {
                throw new BadRequestException("Длина поля annotation должна быть: min = 20, max = 2000");
            }
            event.setAnnotation(annotation);
        }

        Long catId = updateEventAdminRequest.getCategory();
        if (catId != null) {
            Category category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new NotFoundException("Категория с id=" + catId + " не найдена"));
            event.setCategory(category);
        }

        String description = updateEventAdminRequest.getDescription();
        if (description != null) {
            if (description.length() < 20 || description.length() > 7000 || description.isBlank()) {
                throw new BadRequestException("Длина поля description должна быть: min = 20, max = 7000");
            }
            event.setDescription(description);
        }

        LocalDateTime eventDate = updateEventAdminRequest.getEventDate();
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(NUMBER_HOURS_FROM_DATE_PUBLICATION))) {
                throw new BadRequestException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
            }
            event.setEventDate(eventDate);
        }

        Location location = updateEventAdminRequest.getLocation();
        if (location != null) {
            event.setLat(location.getLat());
            event.setLon(location.getLon());
        }

        Boolean paid = updateEventAdminRequest.getPaid();
        if (paid != null) {
            event.setPaid(paid);
        }

        Integer participantLimit = updateEventAdminRequest.getParticipantLimit();
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }

        Boolean requestModeration = updateEventAdminRequest.getRequestModeration();
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }

        EventStateAction stateAction = updateEventAdminRequest.getStateAction();
        if (stateAction != null) {
            if (stateAction == PUBLISH_EVENT) {
                if (event.getState() == PENDING) {
                    event.setState(PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                } else {
                    throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
                }
            }

            if (stateAction == REJECT_EVENT) {
                if (event.getState() != PUBLISHED) {
                    event.setState(CANCELED);
                } else {
                    throw new ConflictException("Событие можно отменить, только если оно в состоянии ожидания публикации");
                }
            }
        }

        String title = updateEventAdminRequest.getTitle();
        if (title != null) {
            if (title.length() < 3 || title.length() > 120 || title.isBlank()) {
                throw new BadRequestException("Длина поля title должна быть: min = 3, max = 120");
            }
            event.setTitle(title);
        }

        String comment = updateEventAdminRequest.getComment();
        if (comment != null && stateAction == REJECT_EVENT) {
            if (comment.length() < 20 || comment.length() > 500 || comment.isBlank()) {
                throw new BadRequestException("Длина поля comment должна быть: min = 20, max = 500");
            }
            List<AdminComment> adminComments;
            if (event.getAdminComments().isEmpty()) {
                adminComments = new ArrayList<>();
            } else {
                adminComments = event.getAdminComments();
            }
            adminComments.add(new AdminComment(comment, LocalDateTime.now()));
            event.setAdminComments(adminComments);
        }

        return event;
    }
}