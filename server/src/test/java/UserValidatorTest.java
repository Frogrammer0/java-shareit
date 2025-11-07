

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validate_WhenValidUserDto_ShouldNotThrowException() {
        UserDto validUserDto = UserDto.builder()
                .name("Valid User")
                .email("valid@email.com")
                .build();

        assertDoesNotThrow(() -> userValidator.validate(validUserDto));
    }

    @Test
    void validate_WhenNullName_ShouldThrowValidationException() {
        UserDto invalidUserDto = UserDto.builder()
                .name(null)
                .email("valid@email.com")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(invalidUserDto));

        assertTrue(exception.getMessage().contains("не указано имя"));
    }

    @Test
    void validate_WhenBlankName_ShouldThrowValidationException() {
        UserDto invalidUserDto = UserDto.builder()
                .name("   ")
                .email("valid@email.com")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(invalidUserDto));

        assertTrue(exception.getMessage().contains("не указано имя"));
    }

    @Test
    void validate_WhenNullEmail_ShouldThrowValidationException() {
        UserDto invalidUserDto = UserDto.builder()
                .name("Valid User")
                .email(null)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(invalidUserDto));

        assertTrue(exception.getMessage().contains("не указан емейл"));
    }

    @Test
    void validate_WhenBlankEmail_ShouldThrowValidationException() {
        UserDto invalidUserDto = UserDto.builder()
                .name("Valid User")
                .email("   ")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(invalidUserDto));

        assertTrue(exception.getMessage().contains("не указан емейл"));
    }

    @Test
    void validate_WhenInvalidEmailFormat_ShouldThrowValidationException() {
        UserDto invalidUserDto = UserDto.builder()
                .name("Valid User")
                .email("invalid-email")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userValidator.validate(invalidUserDto));

        assertTrue(exception.getMessage().contains("Неверный формат адреса почты"));
    }

    @Test
    void isUserExists_WhenUserExists_ShouldNotThrowException() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        assertDoesNotThrow(() -> userValidator.isUserExists(userId));
        verify(userRepository).existsById(userId);
    }

    @Test
    void isUserExists_WhenUserNotExists_ShouldThrowNotFoundException() {
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userValidator.isUserExists(userId));

        assertTrue(exception.getMessage().contains("Пользователь с id"));
        verify(userRepository).existsById(userId);
    }

    @Test
    void isMailExists_WhenEmailNotExists_ShouldNotThrowException() {
        String email = "new@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.isMailExists(email));
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void isMailExists_WhenEmailExists_ShouldThrowDuplicatedDataException() {
        String email = "existing@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class,
                () -> userValidator.isMailExists(email));

        assertTrue(exception.getMessage().contains("Этот имейл уже используется"));
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void validateEmailForOwner_WhenEmailNotExists_ShouldNotThrowException() {
        String email = "new@email.com";
        Long userId = 1L;
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userValidator.validateEmailForOwner(email, userId));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void validateEmailForOwner_WhenEmailExistsButSameUser_ShouldNotThrowException() {
        String email = "user@email.com";
        Long userId = 1L;
        User sameUser = User.builder().id(userId).email(email).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sameUser));

        assertDoesNotThrow(() -> userValidator.validateEmailForOwner(email, userId));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void validateEmailForOwner_WhenEmailExistsForDifferentUser_ShouldThrowDuplicatedDataException() {
        String email = "other@email.com";
        Long userId = 1L;
        Long otherUserId = 2L;
        User otherUser = User.builder().id(otherUserId).email(email).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(otherUser));

        DuplicatedDataException exception = assertThrows(DuplicatedDataException.class,
                () -> userValidator.validateEmailForOwner(email, userId));

        assertTrue(exception.getMessage().contains("Этот имейл уже используется"));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void validateEmailForOwner_WhenNullEmail_ShouldNotThrowException() {
        String email = null;
        Long userId = 1L;

        assertDoesNotThrow(() -> userValidator.validateEmailForOwner(email, userId));
        verify(userRepository, never()).findByEmail(anyString());
    }
}