package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService{
    InMemoryUserStorage userStorage;

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long id) {
        return userStorage.getUserById(id);
    }

    @Override
    public UserDto create(UserDto userDto) {
        return userStorage.create(userDto);
    }

    @Override
    public UserDto update(UserDto userDto) {
        return userStorage.update(userDto);
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }

}
