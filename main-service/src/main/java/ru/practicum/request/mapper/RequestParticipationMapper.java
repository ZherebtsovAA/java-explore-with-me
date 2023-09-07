package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.RequestParticipation;
import ru.practicum.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestParticipationMapper {
    @Mapping(source = "requester", target = "requester")
    @Mapping(source = "event", target = "event")
    @Mapping(target = "id", constant = "-1L")
    RequestParticipation toRequestParticipation(User requester, Event event);

    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    ParticipationRequestDto toParticipationRequestDto(RequestParticipation requestParticipation);

    List<ParticipationRequestDto> toParticipationRequestDto(Iterable<RequestParticipation> requestsParticipation);
}