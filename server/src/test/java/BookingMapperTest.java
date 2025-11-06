
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class BookingMapperTest {

    @InjectMocks
    private BookingMapper bookingMapper;

    @Test
    void toBookingDto_WhenValidBooking_ShouldMapCorrectly() {
        User booker = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 12, 25, 10, 0))
                .end(LocalDateTime.of(2023, 12, 26, 10, 0))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        BookingDto result = bookingMapper.toBookingDto(booking);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(LocalDateTime.of(2023, 12, 25, 10, 0), result.getStart());
        assertEquals(LocalDateTime.of(2023, 12, 26, 10, 0), result.getEnd());
        assertEquals(1L, result.getItemId());
        assertEquals(2L, result.getBookerId());
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void toBookingShortDto_WhenValidBooking_ShouldMapCorrectly() {
        User booker = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 12, 25, 10, 0))
                .end(LocalDateTime.of(2023, 12, 26, 10, 0))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();


        BookingShortDto result = bookingMapper.toBookingShortDto(booking);


        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2L, result.getBookerId());
        assertEquals(LocalDateTime.of(2023, 12, 25, 10, 0), result.getStart());
        assertEquals(LocalDateTime.of(2023, 12, 26, 10, 0), result.getEnd());
    }

    @Test
    void toBookingResponseDto_WhenValidBooking_ShouldMapCorrectly() {

        User owner = User.builder().id(3L).name("Owner").build();
        User booker = User.builder().id(2L).name("Booker").build();
        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .owner(owner)
                .request(null)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 12, 25, 10, 0))
                .end(LocalDateTime.of(2023, 12, 26, 10, 0))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();


        BookingResponseDto result = bookingMapper.toBookingResponseDto(booking);


        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(LocalDateTime.of(2023, 12, 25, 10, 0), result.getStart());
        assertEquals(LocalDateTime.of(2023, 12, 26, 10, 0), result.getEnd());
        assertEquals(Status.APPROVED, result.getStatus());
        assertNotNull(result.getItem());
        assertEquals(1L, result.getItem().getId());
        assertEquals("Test Item", result.getItem().getName());
        assertNull(result.getItem().getRequestId());
        assertEquals(3L, result.getItem().getOwnerId());
        assertNotNull(result.getBooker());
        assertEquals(2L, result.getBooker().getId());
        assertEquals("Booker", result.getBooker().getName());
    }
}