import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.BookingState;
import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class BookingClientTest {

    @Mock
    private RestTemplate restTemplate;

    private BookingClientTestRef bookingClient;

    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(5);

    @BeforeEach
    void setUp() {
        bookingClient = new BookingClientTestRef(restTemplate);
    }

    @Test
    @DisplayName("getBookings - базовый тест")
    void getBookings_basicTest() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok().build());

        bookingClient.getBookings(1L, BookingState.ALL, 0, 10);

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), anyMap());
    }


    @Test
    @DisplayName("getBookingsByUserItem - базовый тест")
    void getBookingsByUserItem_basicTest() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok().build());

        bookingClient.getBookingsByUserItem(1L, BookingState.WAITING, 0, 10);

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), anyMap());
    }
}