package ru.practicum.compilations.dto;

import lombok.*;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Value
public class CompilationDto {
    List<EventShortDto> events;
    Long id;
    Boolean pinned;
    String title;
}