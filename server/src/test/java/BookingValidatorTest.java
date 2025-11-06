import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingValidator;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingValidatorTest {

    private BookingValidator bookingValidator;
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        bookingValidator = new BookingValidator(itemRepository, null);
    }

    @Test
    void validateDate_shouldThrowIfStartAfterEnd() {
        BookingRequestDto dto = BookingRequestDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        assertThrows(ValidationException.class, () -> bookingValidator.validateDate(dto));
    }

    @Test
    void validateDate_shouldThrowIfStartIsPast() {
        BookingRequestDto dto = BookingRequestDto.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        assertThrows(ValidationException.class, () -> bookingValidator.validateDate(dto));
    }

    @Test
    void isItemAvailable_shouldThrowIfNotAvailable() {
        when(itemRepository.existsByIdAndAvailableIsTrue(1L)).thenReturn(false);
        assertThrows(ValidationException.class, () -> bookingValidator.isItemAvailable(1L));
    }
}
