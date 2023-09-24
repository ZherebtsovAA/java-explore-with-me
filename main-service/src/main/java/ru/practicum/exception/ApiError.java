package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static ru.practicum.utils.DtoFormats.DATE_TIME_PATTERN;


@RequiredArgsConstructor
@Getter
@ToString
public class ApiError {
    private final String message;
    private final String reason;
    private final HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private final LocalDateTime timestamp;
}