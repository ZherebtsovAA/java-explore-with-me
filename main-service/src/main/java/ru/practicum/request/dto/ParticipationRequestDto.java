package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import ru.practicum.request.model.RequestParticipationState;
import ru.practicum.utils.DtoFormats;

import java.time.LocalDateTime;

@Value
public class ParticipationRequestDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DtoFormats.DATE_TIME_PATTERN)
    LocalDateTime created;
    long event;
    long id;
    long requester;
    RequestParticipationState status;
}