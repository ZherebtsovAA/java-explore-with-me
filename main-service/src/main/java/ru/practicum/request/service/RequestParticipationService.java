package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestParticipationService {
    List<ParticipationRequestDto> getRequestParticipationCurrentUser(Long userId);

    ParticipationRequestDto save(Long userId, Long eventId);

    ParticipationRequestDto cancelYourParticipationRequest(Long userId, Long requestId);
}