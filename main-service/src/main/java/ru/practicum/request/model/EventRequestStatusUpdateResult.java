package ru.practicum.request.model;

import lombok.Value;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@Value
public class EventRequestStatusUpdateResult {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;
}