package ru.practicum.controller;

import dto.HitDto;
import dto.SearchCriteria;
import dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.exception.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class ClientController {
    private final WebClient webClient;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody HitDto hitDto,
                     HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), hitDto);

        webClient
                .post()
                .uri("/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(hitDto), HitDto.class)
                .retrieve()
                .bodyToMono(ClientResponse.class)
                .block();
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> findStats(@Valid SearchCriteria searchCriteria,
                                        HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        if (searchCriteria.getStart().isAfter(searchCriteria.getEnd())) {
            throw new BadRequestException("проверьте параметры запроса: start, end");
        }

        return webClient
                .post()
                .uri("/stats")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(searchCriteria), SearchCriteria.class)
                .retrieve()
                .bodyToFlux(ViewStatsDto.class)
                .collectList()
                .block();
    }
}