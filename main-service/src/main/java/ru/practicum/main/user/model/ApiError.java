package ru.practicum.main.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import ru.practicum.main.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Getter
@ToString
public class ApiError {
    private final List<String> errors;
    private final String message;
    private final String reason;
    private final HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN)
    private final LocalDateTime timestamp;
}