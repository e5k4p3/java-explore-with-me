package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto addUser(UserDto userDto);

    void deleteUser(Long userId);

    List<UserDto> getAllUsers(Set<Long> ids, Integer from, Integer size);
}
