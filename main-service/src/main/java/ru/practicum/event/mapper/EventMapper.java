package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.AdminCommentDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.AdminComment;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(target = "id", constant = "-1L")
    Event toEvent(NewEventDto newEventDto, Category category, User initiator, Location location);

    @Mapping(source = "event.id", target = "id")
    @Mapping(source = "categoryDto", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "adminComments", target = "adminComments")
    EventFullDto toEventFullDto(Event event, CategoryDto categoryDto, int confirmedRequests,
                                UserShortDto initiator, int views, List<AdminCommentDto> adminComments);

    @Mapping(source = "confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "views", target = "views")
    EventShortDto toEventShortDto(Event event, int confirmedRequests, int views);

    AdminCommentDto toAdminCommentDto(AdminComment adminComment);

    List<AdminCommentDto> toAdminCommentDto(Iterable<AdminComment> adminComment);
}