package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.UpdateEventAdminRequest;
import ru.practicum.event.service.EventAdminService;
import ru.practicum.utils.CustomPageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventAdminController {
    private final EventAdminService eventAdminService;

    @GetMapping
    public List<EventFullDto> getEventFull(@RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false) List<EventState> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size,
                                           HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return eventAdminService.getEventFull(users, states, categories, rangeStart, rangeEnd, CustomPageRequest.getPageRequest(from, size));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchUpdateEvent(@PathVariable @Positive Long eventId,
                                         @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                                         HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), updateEventAdminRequest);

        return eventAdminService.patchUpdateEvent(eventId, updateEventAdminRequest);
    }
}