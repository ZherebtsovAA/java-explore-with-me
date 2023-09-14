package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.RequestParticipation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestParticipationMapper {
    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    ParticipationRequestDto toParticipationRequestDto(RequestParticipation requestParticipation);

    List<ParticipationRequestDto> toParticipationRequestDto(Iterable<RequestParticipation> requestsParticipation);
}