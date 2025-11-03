package ru.practicum.shareit.user;

import exceptions.DuplicatedDataException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import user.dto.UserDto;

@Slf4j
@Component
public class UserValidator {
    private final UserRepository userRepository;

    @Autowired
    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
        if (userRepository.existsByEmail(email)) {
            log.error("введен уже использующийся имейл");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }


}
