package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {


    List<UserDto> getAllUsers(int from, int size);

    UserDto getUserById(long id);

    UserDto create(UserDto userDto);

    void delete(Long id);

    UserDto edit(long userId, UserDto userDto);
}
