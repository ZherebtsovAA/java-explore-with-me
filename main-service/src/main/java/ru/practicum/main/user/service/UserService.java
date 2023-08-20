package ru.practicum.main.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.NewUserRequest;

import java.util.List;

public interface UserService {
    List<UserDto> findAll(List<Long> ids, PageRequest pageRequest);

    UserDto save(NewUserRequest newUserRequest);

    void deleteById(Long userId);
}