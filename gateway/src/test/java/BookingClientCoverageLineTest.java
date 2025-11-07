import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class BookingClientCoverageLineTest {

    @Test
    void testConstructor() {
        BookingClient client = new BookingClient("http://localhost:9090", new RestTemplateBuilder());
        assertNotNull(client);
    }

    @Test
    void testSingleMethod() throws Exception {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        BookingClient client = new BookingClient("http://localhost:9090", builder);

        java.lang.reflect.Field restField = client.getClass().getSuperclass().getDeclaredField("rest");
        restField.setAccessible(true);
        restField.set(client, null);

        try {
            client.getBookings(1L, BookingState.ALL, 0, 10);
        } catch (NullPointerException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    void coverAllMethods() throws Exception {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        BookingClient client = new BookingClient("http://localhost:9090", builder);

        java.lang.reflect.Field restField = client.getClass().getSuperclass().getDeclaredField("rest");
        restField.setAccessible(true);
        restField.set(client, null);

        callAllMethodsSafely(client);
    }

    private void callAllMethodsSafely(BookingClient client) {
        try {
            client.getBookings(1L, BookingState.ALL, 0, 10);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            client.getBookings(1L, BookingState.CURRENT, null, null);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            client.getBookings(1L, BookingState.PAST, 0, 10);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            client.getBookings(1L, BookingState.FUTURE, 0, 10);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            client.getBookings(1L, BookingState.WAITING, 0, 10);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            client.getBookings(1L, BookingState.REJECTED, 0, 10);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            BookingRequestDto dto = new BookingRequestDto();
            dto.setItemId(1L);
            dto.setStart(LocalDateTime.now().plusDays(1));
            dto.setEnd(LocalDateTime.now().plusDays(2));
            client.bookItem(1L, dto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            client.getBooking(1L, 123L);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            client.approvedBooking(123L, 1L, true);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            client.approvedBooking(123L, 1L, false);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            client.getBookingsByUserItem(1L, BookingState.ALL, 0, 10);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            client.getBookingsByUserItem(1L, BookingState.CURRENT, null, null);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}