package ru.practicum.compilations.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.Compilations;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationsMapper {
    Compilations toCompilations(NewCompilationDto newCompilationDto);

    @Mapping(source = "events", target = "events")
    CompilationDto toCompilationDto(Compilations compilations, List<EventShortDto> events);
}