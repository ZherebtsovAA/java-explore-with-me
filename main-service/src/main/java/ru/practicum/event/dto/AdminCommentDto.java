package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import ru.practicum.utils.DtoFormats;

import java.time.LocalDateTime;

@Value
public class AdminCommentDto {
    String comment;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DtoFormats.DATE_TIME_PATTERN)
    LocalDateTime createdOn;
}