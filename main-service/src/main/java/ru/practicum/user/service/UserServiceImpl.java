package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.NewUserRequest;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findAll(List<Long> ids, PageRequest pageRequest) {
        Page<User> users = ids == null
                ? userRepository.findAll(pageRequest)
                : userRepository.findAllByIdIn(ids, pageRequest);

        return userMapper.toUserDto(users.getContent());
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    @Transactional
    @Override
    public UserDto save(NewUserRequest newUserRequest) {
        User templateSearchUser = new User();
        templateSearchUser.setEmail(newUserRequest.getEmail());
        if (userRepository.exists(Example.of(templateSearchUser))) {
            throw new ConflictException("Пользователь с таким же email=" + newUserRequest.getEmail() + " уже зарегистрирован");
        }

        User user = userRepository.save(userMapper.toUser(newUserRequest));

        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void deleteById(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }
}