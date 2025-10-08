package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    InMemoryUserStorage userStorage;

    @Autowired
    public UserServiceImpl(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long id) {
        log.info("получение пользователя в UserService id = {}", id);
        return userStorage.getUserById(id);
    }

    @Override
    public UserDto create(UserDto userDto) {
        return userStorage.create(userDto);
    }


    @Override
    public UserDto edit(long userId, UserDto userDto) {
        return userStorage.edit(userId, userDto);
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }

}
