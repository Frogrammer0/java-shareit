package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryUserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public UserDto getUserById(long id) {
        isUserExist(id);
        return UserMapper.toUserDto(users.get(id));
    }

    public User getRowUserById(long id) {
        return users.get(id);
    }

    public UserDto create(UserDto userDto) {
        isMailExist(userDto.getEmail());

        User user = UserMapper.toUser(userDto);

        user.setId(getNextId());
        log.info("пользователю присвоен id = {}", user.getId());

        users.put(user.getId(), user);
        log.info("пользователь добавлен в базу");


        return UserMapper.toUserDto(user);
    }

    public UserDto edit(long userId, UserDto newUserDto) {

        isUserExist(userId);
        notNullId(userId);
        isMailExist(newUserDto.getEmail());

        User oldUser = users.get(userId);

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

    public void delete(Long id) {
        isUserExist(id);
        notNullId(id);

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
        currentMaxId++;
        log.info("создан id = {}", currentMaxId);
        return currentMaxId;
    }

    private void isUserExist(long userId) {
        if (!users.containsKey(userId)) {
            log.error("пользователь с введенным id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    private void notNullId(long id) {
        if (id == 0) {
            log.error("введен id равный 0");
            throw new ValidationException("Id должен быть указан");
        }

    }

    private void isMailExist(String email) {
        boolean isMailExist = users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(mail -> mail.equals(email));
        if (isMailExist) {
            log.error("введен уже использующийся имейл");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }


}
