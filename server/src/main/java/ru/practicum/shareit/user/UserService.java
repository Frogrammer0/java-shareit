package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {


    List<UserDto> getAllUsers();

    UserDto getUserById(long id);

    UserDto create(UserDto userDto);

    void delete(Long id);

    UserDto edit(long userId, UserDto userDto);
}
