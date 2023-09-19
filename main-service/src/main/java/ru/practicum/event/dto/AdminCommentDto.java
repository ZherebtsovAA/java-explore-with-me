package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import ru.practicum.utils.Constants;

import java.time.LocalDateTime;

@Value
public class AdminCommentDto {
    String comment;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    LocalDateTime createdOn;
}