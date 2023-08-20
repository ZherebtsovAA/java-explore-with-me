package ru.practicum.main.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.NewUserRequest;
import ru.practicum.main.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    List<UserDto> toUserDto(List<User> users);

    User toUser(UserDto userDto);

    User toUser(NewUserRequest newUserRequest);
}