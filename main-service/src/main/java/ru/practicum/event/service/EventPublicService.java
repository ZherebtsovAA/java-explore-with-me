package ru.practicum.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.event.controller.EventSort;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventPublicService {
    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort,
                                  PageRequest pageRequest, String uri, String ip);

    EventFullDto getEvent(Long eventId, String uri, String ip);
}