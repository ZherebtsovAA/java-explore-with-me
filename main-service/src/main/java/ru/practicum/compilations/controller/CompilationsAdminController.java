package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.UpdateCompilationRequest;
import ru.practicum.compilations.service.CompilationsAdminService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationsAdminController {
    private final CompilationsAdminService compilationsAdminService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto newCompilationDto,
                                 HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), newCompilationDto);

        return compilationsAdminService.save(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long compId,
                       HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}'",
                request.getMethod(), request.getRequestURI());

        compilationsAdminService.deleteById(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto patchUpdate(@PathVariable @Positive Long compId,
                                      @RequestBody UpdateCompilationRequest updateCompilationRequest,
                                      HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', RequestBody: '{}'",
                request.getMethod(), request.getRequestURI(), updateCompilationRequest);

        return compilationsAdminService.patchUpdate(compId, updateCompilationRequest);
    }
}