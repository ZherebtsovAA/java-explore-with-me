package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestParticipationService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestParticipationPrivateController {
    private final RequestParticipationService requestParticipationService;

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestParticipationCurrentUser(@PathVariable @Positive Long userId,
                                                                            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}'", request.getMethod(), request.getRequestURI());

        return requestParticipationService.getRequestParticipationCurrentUser(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ParticipationRequestDto createRequestParticipation(@PathVariable @Positive Long userId,
                                                              @RequestParam @Positive Long eventId,
                                                              HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return requestParticipationService.save(userId, eventId);
    }


    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelYourParticipationRequest(@PathVariable @Positive Long userId,
                                                                  @PathVariable @Positive Long requestId,
                                                                  HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}'", request.getMethod(), request.getRequestURI());

        return requestParticipationService.cancelYourParticipationRequest(userId, requestId);
    }
}