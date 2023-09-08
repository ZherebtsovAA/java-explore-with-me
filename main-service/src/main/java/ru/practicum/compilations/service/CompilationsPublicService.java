package ru.practicum.compilations.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.compilations.dto.CompilationDto;

import java.util.List;

public interface CompilationsPublicService {
    List<CompilationDto> findAll(Boolean pinned, PageRequest pageRequest);

    CompilationDto findById(Long compId);
}