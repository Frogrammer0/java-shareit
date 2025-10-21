package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@Component
public class UserValidator {
    private UserRepository userRepository;

    public void validate(UserDto userDto) {
        validateEmail(userDto.getEmail());
        validateName(userDto.getName());
    }

    void validateEmail(String email) {
        log.info("валидация почты");

        if (email == null || email.isBlank()) {
            log.error("не указана почта");
            throw new ValidationException("не указан емейл");
        }

        if (!email.contains("@")) {
            log.error("почта введена в неверном формате");
            throw new ValidationException("Неверный формат адреса почты: " + email);
        }
    }

    void validateName(String name) {
        log.info("валидация имени");
        if (name == null || name.isBlank()) {
            log.error("не указано имя");
            throw new ValidationException("не указано имя");
        }
    }

    public void isUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("пользователь с введенным id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    public void isMailExists(String email) {
        if (!userRepository.existsByEmail(email)) {
            log.error("введен уже использующийся имейл");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }


}
