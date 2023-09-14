package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.HitDto;
import ru.practicum.stats.dto.SearchCriteriaDto;
import ru.practicum.stats.ViewStatsDto;
import ru.practicum.stats.exception.BadRequestException;
import ru.practicum.stats.service.StatsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody HitDto hitDto,
                     HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), hitDto);

        statsService.save(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> findStats(@Valid SearchCriteriaDto searchCriteriaDto,
                                        HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        if (searchCriteriaDto.getStart().isAfter(searchCriteriaDto.getEnd())) {
            throw new BadRequestException("проверьте параметры запроса: start, end");
        }

        return statsService.findStats(searchCriteriaDto);
    }
}