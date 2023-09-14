package ru.practicum.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.UpdateEventUserRequest;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.EventRequestStatusUpdateResult;

import java.util.List;

public interface EventPrivateService {
    List<EventShortDto> getEventShort(Long userId, PageRequest pageRequest);

    EventFullDto save(Long userId, NewEventDto newEventDto);

    EventFullDto getEventFull(Long userId, Long eventId);

    EventFullDto patchUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getParticipationRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult patchUpdateStatus(Long userId, Long eventId,
                                                     EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    Event findById(Long eventId);
}