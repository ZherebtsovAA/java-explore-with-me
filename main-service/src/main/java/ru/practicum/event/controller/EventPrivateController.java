package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.UpdateEventUserRequest;
import ru.practicum.event.service.EventPrivateService;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.EventRequestStatusUpdateResult;
import ru.practicum.utils.CustomPageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventPrivateController {
    private final EventPrivateService eventPrivateService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventShort(@PathVariable @Positive Long userId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size,
                                             HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return eventPrivateService.getEventShort(userId, CustomPageRequest.getPageRequest(from, size));
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto create(@PathVariable @Positive Long userId,
                               @RequestBody @Valid NewEventDto newEventDto,
                               HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), newEventDto);

        return eventPrivateService.save(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventFull(@PathVariable @Positive Long userId,
                                     @PathVariable @Positive Long eventId,
                                     HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}'",
                request.getMethod(), request.getRequestURI());

        return eventPrivateService.getEventFull(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto patchUpdateEvent(@PathVariable @Positive Long userId,
                                         @PathVariable @Positive Long eventId,
                                         @RequestBody UpdateEventUserRequest updateEventUserRequest,
                                         HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), updateEventUserRequest);

        return eventPrivateService.patchUpdateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequest(@PathVariable @Positive Long userId,
                                                                 @PathVariable @Positive Long eventId,
                                                                 HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}'",
                request.getMethod(), request.getRequestURI());

        return eventPrivateService.getParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult patchUpdateStatus(@PathVariable @Positive Long userId,
                                                            @PathVariable @Positive Long eventId,
                                                            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), eventRequestStatusUpdateRequest);

        return eventPrivateService.patchUpdateStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }
}