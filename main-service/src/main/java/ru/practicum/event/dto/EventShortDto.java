package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.utils.DtoFormats;

import java.time.LocalDateTime;

@Value
public class EventShortDto {
    String annotation;
    CategoryDto category;
    int confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DtoFormats.DATE_TIME_PATTERN)
    LocalDateTime eventDate;
    long id;
    UserShortDto initiator;
    boolean paid;
    String title;
    int views;
}