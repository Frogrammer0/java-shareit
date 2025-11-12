package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class UserValidator {
    private final UserRepository userRepository;

    @Autowired
    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void isUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("пользователь с введенным id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    public void isMailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            log.error("введен уже использующийся имейл");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }

    public void validateEmailForOwner(String email, long userId) {

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent() && userOpt.get().getId() != userId) {
                log.error("Имейл {} уже используется другим пользователем id={}", email, userOpt.get().getId());
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
        }

}
