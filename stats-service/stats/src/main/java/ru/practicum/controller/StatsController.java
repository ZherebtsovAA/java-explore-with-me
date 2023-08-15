package ru.practicum.controller;

import dto.HitDto;
import dto.SearchCriteria;
import dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.StatsService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody HitDto hitDto,
                     HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), hitDto);

        statsService.save(hitDto);
    }

    @PostMapping("/stats")
    public List<ViewStatsDto> findStats(@RequestBody SearchCriteria searchCriteria,
                                        HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), searchCriteria);

        return statsService.findStats(searchCriteria);
    }
}