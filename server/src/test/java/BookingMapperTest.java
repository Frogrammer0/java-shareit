
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapper();

    @Test
    void toBookingDto_ShouldMapCorrectly() {
        User booker = User.builder().id(1L).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).owner(owner).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        BookingDto result = bookingMapper.toBookingDto(booking);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getItemId());
        assertEquals(1L, result.getBookerId());
        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    void toBooking_ShouldMapCorrectly() {
        BookingRequestDto requestDto = BookingRequestDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();
        Item item = Item.builder().id(1L).build();
        User booker = User.builder().id(1L).build();

        Booking result = bookingMapper.toBooking(requestDto, item, booker);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(item, result.getItem());
        assertEquals(booker, result.getBooker());
    }

    @Test
    void toBookingRequestDto_ShouldMapCorrectly() {
        Item item = Item.builder().id(1L).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .build();

        BookingRequestDto result = bookingMapper.toBookingRequestDto(booking);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getItemId());
    }

    @Test
    void toBookingResponseDto_ShouldMapCorrectly() {
        User booker = User.builder().id(1L).name("Booker").build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).name("Item").owner(owner).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        BookingResponseDto result = bookingMapper.toBookingResponseDto(booking);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Status.APPROVED, result.getStatus());
        assertNotNull(result.getItem());
        assertNotNull(result.getBooker());
    }

    @Test
    void toBookingShortDto_ShouldMapCorrectly() {
        User booker = User.builder().id(1L).build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .booker(booker)
                .build();

        BookingShortDto result = bookingMapper.toBookingShortDto(booking);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getBookerId());
    }

    @Test
    void allMethods_WithNull_ShouldReturnNull() {
        assertNull(bookingMapper.toBookingDto(null));
        assertNull(bookingMapper.toBooking(null, null, null));
        assertNull(bookingMapper.toBookingRequestDto(null));
        assertNull(bookingMapper.toBookingResponseDto(null));
        assertNull(bookingMapper.toBookingShortDto(null));
    }
}