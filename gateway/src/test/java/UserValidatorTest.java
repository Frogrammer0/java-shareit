import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserValidator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validateEmail_WhenInvalid_ShouldThrowException() {
        assertAll(
                () -> {
                    ValidationException exception = assertThrows(ValidationException.class,
                            () -> userValidator.validateEmail(null));
                    assertEquals("не указан емейл", exception.getMessage());
                },
                () -> {
                    ValidationException exception = assertThrows(ValidationException.class,
                            () -> userValidator.validateEmail(""));
                    assertEquals("не указан емейл", exception.getMessage());
                },
                () -> {
                    ValidationException exception = assertThrows(ValidationException.class,
                            () -> userValidator.validateEmail("invalid-email"));
                    assertEquals("Неверный формат адреса почты: invalid-email", exception.getMessage());
                }
        );
    }

    @Test
    void validateName_WhenInvalid_ShouldThrowException() {
        assertAll(
                () -> assertThrows(ValidationException.class, () -> userValidator.validateName(null)),
                () -> assertThrows(ValidationException.class, () -> userValidator.validateName("")),
                () -> assertThrows(ValidationException.class, () -> userValidator.validateName("   "))
        );
    }
}