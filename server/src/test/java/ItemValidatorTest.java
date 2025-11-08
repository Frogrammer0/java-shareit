import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemValidatorTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    private ItemValidator itemValidator;

    @BeforeEach
    void setUp() {
        itemValidator = new ItemValidator(itemRepository, bookingRepository);
    }

    @Test
    void hasAccess_WhenUserHasAccess_ShouldNotThrowException() {
        long itemId = 1L;
        long userId = 1L;
        when(itemRepository.existsByIdAndOwnerId(itemId, userId)).thenReturn(true);

        assertDoesNotThrow(() -> itemValidator.hasAccess(itemId, userId));
        verify(itemRepository).existsByIdAndOwnerId(itemId, userId);
    }

    @Test
    void hasAccess_WhenUserHasNoAccess_ShouldThrowForbiddenException() {
        long itemId = 1L;
        long userId = 1L;
        when(itemRepository.existsByIdAndOwnerId(itemId, userId)).thenReturn(false);

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> itemValidator.hasAccess(itemId, userId));
        assertEquals("отсутствие прав доступа на изменения ресурса", exception.getMessage());
        verify(itemRepository).existsByIdAndOwnerId(itemId, userId);
    }

    @Test
    void isItemExists_WhenItemExists_ShouldNotThrowException() {
        long itemId = 1L;
        when(itemRepository.existsById(itemId)).thenReturn(true);

        assertDoesNotThrow(() -> itemValidator.isItemExists(itemId));
        verify(itemRepository).existsById(itemId);
    }

    @Test
    void isItemExists_WhenItemDoesNotExist_ShouldThrowNotFoundException() {
        long itemId = 1L;
        when(itemRepository.existsById(itemId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemValidator.isItemExists(itemId));
        assertEquals("Вещь с id = 1 не найдена", exception.getMessage());
        verify(itemRepository).existsById(itemId);
    }

}