package ru.practicum.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main.user.model.ApiError;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class CustomRestExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException exception) {
        ApiError apiError = new ApiError(exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND, LocalDateTime.now());
        log.warn("Exception occurred: {}", apiError.toString(), exception);

        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException exception) {
        ApiError apiError = new ApiError(exception.getMessage(), HttpStatus.CONFLICT.getReasonPhrase(),
                HttpStatus.CONFLICT, LocalDateTime.now());
        log.warn("Exception occurred: {}", apiError.toString(), exception);

        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final BadRequestException exception) {
        ApiError apiError = new ApiError(exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST, LocalDateTime.now());
        log.warn("Exception occurred: {}", apiError.toString(), exception);

        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        ApiError apiError = new ApiError(exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST, LocalDateTime.now());
        log.warn("Exception occurred: {}", apiError.toString(), exception);

        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(final ConstraintViolationException exception) {
        ApiError apiError = new ApiError(exception.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST, LocalDateTime.now());
        log.warn("Exception occurred: {}", apiError.toString(), exception);

        return apiError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable exception) {
        ApiError apiError = new ApiError(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
        log.warn("Exception occurred: {}", apiError.toString(), exception);

        return apiError;
    }
}