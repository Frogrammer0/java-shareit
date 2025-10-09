package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAllUsers() {
        log.info("запрос всех пользователей");
        return users.values();
    }

    public Optional<User> getUserById(long id) {
        log.info("запрос пользователя с id = {}", id);
        return Optional.of(users.get(id));
    }

    public User create(User user) {

        user.setId(getNextId());
        log.info("пользователю присвоен id = {}", user.getId());

        users.put(user.getId(), user);
        log.info("пользователь добавлен в базу");

        return user;
    }

    public User edit(long userId, User newUser) {
        User oldUser = users.get(userId);

        if (newUser.getName() != null) {
            if (!newUser.getName().isBlank()) {
                oldUser.setName(newUser.getName());
                log.info("изменено имя пользователя");
            }
        }

        if (newUser.getEmail() != null) {
            if (!newUser.getEmail().isBlank() || newUser.getEmail().contains("@")) {
                oldUser.setEmail(newUser.getEmail());
                log.info("изменена почта пользователя");
            }
        }

        return oldUser;
    }

    public void delete(Long id) {
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

    public void isUserExist(long userId) {
        if (!users.containsKey(userId)) {
            log.error("пользователь с введенным id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    public void isMailExist(String email) {
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
