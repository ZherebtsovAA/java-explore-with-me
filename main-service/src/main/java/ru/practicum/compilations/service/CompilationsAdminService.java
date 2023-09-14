package ru.practicum.compilations.service;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.UpdateCompilationRequest;

public interface CompilationsAdminService {
    CompilationDto save(NewCompilationDto newCompilationDto);

    void deleteById(Long compId);

    CompilationDto patchUpdate(Long compId, UpdateCompilationRequest updateCompilationRequest);
}