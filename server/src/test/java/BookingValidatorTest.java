import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingValidator;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingValidatorTest {

    @Mock
    private ItemRepository itemRepository;

    private BookingValidator bookingValidator;

    @BeforeEach
    void setUp() {
        bookingValidator = new BookingValidator(itemRepository, null);
    }

    @Test
    void isItemAvailable_WhenItemIsAvailable_ShouldNotThrowException() {
        long itemId = 1L;
        when(itemRepository.existsByIdAndAvailableIsTrue(itemId)).thenReturn(true);

        assertDoesNotThrow(() -> bookingValidator.isItemAvailable(itemId));
        verify(itemRepository).existsByIdAndAvailableIsTrue(itemId);
    }

    @Test
    void isItemAvailable_WhenItemIsNotAvailable_ShouldThrowValidationException() {
        long itemId = 1L;
        when(itemRepository.existsByIdAndAvailableIsTrue(itemId)).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingValidator.isItemAvailable(itemId));
        assertEquals("вещь с id = 1  недоступна", exception.getMessage());
        verify(itemRepository).existsByIdAndAvailableIsTrue(itemId);
    }

    @Test
    void isBookerOrOwner_WhenUserIsBooker_ShouldNotThrowException() {
        long userId = 1L;
        Booking booking = createBookingWithBookerAndOwner(1L, 2L);

        assertDoesNotThrow(() -> bookingValidator.isBookerOrOwner(userId, booking));
    }

    @Test
    void isBookerOrOwner_WhenUserIsOwner_ShouldNotThrowException() {
        long userId = 2L;
        Booking booking = createBookingWithBookerAndOwner(1L, 2L);

        assertDoesNotThrow(() -> bookingValidator.isBookerOrOwner(userId, booking));
    }

    @Test
    void isBookerOrOwner_WhenUserIsNeitherBookerNorOwner_ShouldThrowForbiddenException() {
        long userId = 3L;
        Booking booking = createBookingWithBookerAndOwner(1L, 2L);

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> bookingValidator.isBookerOrOwner(userId, booking));
        assertEquals("отсутствие доступа у пользователя с id = 3", exception.getMessage());
    }

    @Test
    void isOwner_WhenUserIsOwner_ShouldNotThrowException() {
        long userId = 2L;
        Booking booking = createBookingWithBookerAndOwner(1L, 2L);

        assertDoesNotThrow(() -> bookingValidator.isOwner(userId, booking));
    }

    @Test
    void isOwner_WhenUserIsNotOwner_ShouldThrowForbiddenException() {
        long userId = 1L;
        Booking booking = createBookingWithBookerAndOwner(1L, 2L);

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> bookingValidator.isOwner(userId, booking));
        assertEquals("отсутствие доступа у пользователя с id = 1", exception.getMessage());
    }

    @Test
    void hasItem_WhenUserHasItems_ShouldNotThrowException() {
        long userId = 1L;
        when(itemRepository.existsByOwnerId(userId)).thenReturn(true);

        assertDoesNotThrow(() -> bookingValidator.hasItem(userId));
        verify(itemRepository).existsByOwnerId(userId);
    }

    @Test
    void hasItem_WhenUserHasNoItems_ShouldThrowNotFoundException() {
        long userId = 1L;
        when(itemRepository.existsByOwnerId(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingValidator.hasItem(userId));
        assertEquals("отсутствие вещей у пользователя с id = 1", exception.getMessage());
        verify(itemRepository).existsByOwnerId(userId);
    }

    @Test
    void statusIsWaiting_WhenStatusIsWaiting_ShouldNotThrowException() {
        Booking booking = new Booking();
        booking.setStatus(Status.WAITING);

        assertDoesNotThrow(() -> bookingValidator.statusIsWaiting(booking));
    }

    @Test
    void statusIsWaiting_WhenStatusIsNotWaiting_ShouldThrowValidationException() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingValidator.statusIsWaiting(booking));
        assertEquals("неверный статус заявки", exception.getMessage());
    }

    private Booking createBookingWithBookerAndOwner(long bookerId, long ownerId) {
        Booking booking = new Booking();

        User booker = new User();
        booker.setId(bookerId);
        booking.setBooker(booker);

        Item item = new Item();
        User owner = new User();
        owner.setId(ownerId);
        item.setOwner(owner);
        booking.setItem(item);

        return booking;
    }
}