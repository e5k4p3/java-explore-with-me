package ru.practicum.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.EntityAlreadyExistsException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;
import ru.practicum.user.util.UserMapper;
import ru.practicum.util.PageableMaker;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        checkUserEmailExistence(userDto.getEmail());
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info("Пользователь с email " + user.getEmail() + " был добавлен.");
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        checkUserIdExistence(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с id " + userId + " был удален.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(Set<Long> ids, Integer from, Integer size) {
        if (ids.isEmpty()) {
            return userRepository.findAll(PageableMaker.makePage(from, size)).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        return userRepository.findAllByIdIn(ids, PageableMaker.makePage(from, size)).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void checkUserIdExistence(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    private void checkUserEmailExistence(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EntityAlreadyExistsException("Пользователь с email " + email + " уже существует.");
        }
    }
}
