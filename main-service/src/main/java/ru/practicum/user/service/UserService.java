package ru.practicum.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.NewUserRequest;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> findAll(List<Long> ids, PageRequest pageRequest);

    User findById(Long userId);

    UserDto save(NewUserRequest newUserRequest);

    void deleteById(Long userId);
}