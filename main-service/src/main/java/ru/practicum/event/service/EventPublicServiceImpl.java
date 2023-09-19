package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.event.controller.EventSort;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.EventSpecifications;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.event.model.EventState.PUBLISHED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventStatisticsService eventStatisticsService;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort,
                                         PageRequest pageRequest, String uri, String ip) {

        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new BadRequestException("Значение поля rangeStart должно быть ранее значения поля rangeEnd");
            }
        }

        Specification<Event> searchCriteria = Specification
                .where(
                        EventSpecifications.belongsToStates(List.of(PUBLISHED))
                                .and(text == null || text.isBlank() ? null : EventSpecifications.containsToAnnotationCaseInsensitive(text)
                                        .or(EventSpecifications.containsToDescriptionCaseInsensitive(text)))
                                .and(categories == null || categories.isEmpty() ? null : EventSpecifications.belongsToCategories(categories))
                                .and(paid == null ? null : EventSpecifications.isPaid(paid))
                                .and(rangeStart == null ? null : EventSpecifications.eventDateGreaterThan(rangeStart))
                                .and(rangeEnd == null ? null : EventSpecifications.eventDatelessThan(rangeEnd))
                                .and(rangeStart == null && rangeEnd == null ? EventSpecifications.eventDateGreaterThan(LocalDateTime.now()) : null)
                );

        List<Event> foundEvents = eventRepository
                .findAll(searchCriteria, pageRequest)
                .getContent();

        Map<Long, Integer> confirmedRequests = eventStatisticsService.getConfirmedRequests(foundEvents);

        foundEvents = foundEvents.stream()
                .filter(event -> {
                    if (onlyAvailable) {
                        return Objects.equals(event.getParticipantLimit(), confirmedRequests.getOrDefault(event.getId(), 0));
                    } else {
                        return event.getParticipantLimit() > confirmedRequests.getOrDefault(event.getId(), 0);
                    }
                })
                .collect(Collectors.toList());

        Map<Long, Integer> views = eventStatisticsService.getViews(foundEvents);

        List<EventShortDto> resultList = new ArrayList<>();
        for (Event event : foundEvents) {
            resultList.add(eventMapper.toEventShortDto(
                    event,
                    confirmedRequests.getOrDefault(event.getId(), 0),
                    views.getOrDefault(event.getId(), 0)));
        }

        // Вариант сортировки: по количеству просмотров
        if (sort == EventSort.VIEWS) {
            resultList = resultList.stream()
                    .sorted(Comparator.comparingInt(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }

        eventStatisticsService.saveStats(uri, ip);

        return resultList;
    }

    @Override
    public EventFullDto getEvent(Long eventId, String uri, String ip) {
        Specification<Event> searchCriteria = Specification
                .where(EventSpecifications.belongsToId(eventId))
                .and(EventSpecifications.belongsToStates(List.of(PUBLISHED)));

        Event event = eventRepository.findOne(searchCriteria)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено или недоступно"));

        Map<Long, Integer> views = eventStatisticsService.getViews(List.of(event));
        Map<Long, Integer> confirmedRequests = eventStatisticsService.getConfirmedRequests(List.of(event));

        eventStatisticsService.saveStats(uri, ip);

        return eventMapper.toEventFullDto(
                event,
                categoryMapper.toCategoryDto(event.getCategory()),
                confirmedRequests.getOrDefault(event.getId(), 0),
                userMapper.toUserShortDto(event.getInitiator()),
                views.getOrDefault(event.getId(), 0),
                null);
    }
}