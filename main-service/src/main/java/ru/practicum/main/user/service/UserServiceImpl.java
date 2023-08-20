package ru.practicum.main.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.model.NewUserRequest;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findAll(List<Long> ids, PageRequest pageRequest) {
        Page<User> users = ids == null
                ? repository.findAll(pageRequest)
                : repository.findAllByIdIn(ids, pageRequest);

        return userMapper.toUserDto(users.getContent());
    }

    @Transactional
    @Override
    public UserDto save(NewUserRequest newUserRequest) {
        User user = repository.save(userMapper.toUser(newUserRequest));
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void deleteById(Long userId) {
        repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        repository.deleteById(userId);
    }
}