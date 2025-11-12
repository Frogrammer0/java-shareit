import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingValidator;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exceptions.ValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingValidatorTest {

    @InjectMocks
    private BookingValidator bookingValidator;

    @Test
    void validateDate_WhenStartIsNull_ShouldThrowException() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setStart(null);
        dto.setEnd(LocalDateTime.now().plusHours(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingValidator.validateDate(dto));
        assertEquals("Дата начала и окончания бронирования не могут быть пустыми", exception.getMessage());
    }

    @Test
    void validateDate_WhenEndIsNull_ShouldThrowException() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingValidator.validateDate(dto));
        assertEquals("Дата начала и окончания бронирования не могут быть пустыми", exception.getMessage());
    }

    @Test
    void validateDate_WhenStartAfterEnd_ShouldThrowException() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingValidator.validateDate(dto));
        assertEquals("Дата начала бронирования должна быть раньше даты окончания", exception.getMessage());
    }

    @Test
    void validateDate_WhenStartEqualsEnd_ShouldThrowException() {
        LocalDateTime sameTime = LocalDateTime.now().plusHours(1);
        BookingRequestDto dto = new BookingRequestDto();
        dto.setStart(sameTime);
        dto.setEnd(sameTime);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingValidator.validateDate(dto));
        assertEquals("Дата начала бронирования должна быть раньше даты окончания", exception.getMessage());
    }

    @Test
    void validateDate_WhenStartInPast_ShouldThrowException() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setStart(LocalDateTime.now().minusHours(1));
        dto.setEnd(LocalDateTime.now().plusHours(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingValidator.validateDate(dto));
        assertEquals("Дата начала бронирования не может быть в прошлом", exception.getMessage());
    }
}