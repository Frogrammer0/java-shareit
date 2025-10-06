package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class InMemoryUserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(users.get(id));
    }

    public User getRowUser(long id) {
        return users.get(id);
    }

    public UserDto create(UserDto userDto) {
        boolean isMailExist = users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(mail -> mail.equals(userDto.getEmail()));
        if (isMailExist) {
            log.error("введен уже использующийся имейл");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        User user = UserMapper.toUser(userDto);

        user.setId(getNextId());
        log.info("пользователю присвоен id= {}", userDto.getId());

        users.put(user.getId(), user);
        log.info("пользователь добавлен в базу");


        return UserMapper.toUserDto(user);
    }

    public UserDto update(UserDto newUserDto) {
        if (newUserDto.getId() == 0) {
            log.error("введен id равный 0");
            throw new ValidationException("Id должен быть указан");
        }


        if (users.containsKey(newUserDto.getId())) {
            User oldUser = users.get(newUserDto.getId());

            // если пользователь найден и все условия соблюдены, обновляем его содержимое
            if (newUserDto.getName() != null) {
                oldUser.setName(newUserDto.getName());
                log.info("изменено имя пользователя");
            }

            if (newUserDto.getEmail() != null) {
                oldUser.setEmail(newUserDto.getEmail());
                log.info("изменена почта пользователя");
            }


            return UserMapper.toUserDto(oldUser);
        }
        log.error("пользователь с введенным id не найден");
        throw new NotFoundException("Пользователь с id = " + newUserDto.getId() + " не найден");
    }

    public void delete(Long id) {
        if (id == 0) {
            log.error("введен id равный 0");
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(id)) {
            log.error("пользователь с введенным id не найден");
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        users.remove(id);
        log.info("пользователь с id = {} удален", id);
    }


    private Long getNextId() {
        log.info("создан id");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
