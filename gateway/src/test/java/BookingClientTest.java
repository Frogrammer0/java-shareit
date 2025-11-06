import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Тестовый wrapper класс который наследует BaseClient напрямую
class TestBookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public TestBookingClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get(API_PREFIX + "?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> bookItem(long userId, BookingRequestDto requestDto) {
        return post(API_PREFIX, userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get(API_PREFIX + "/" + bookingId, userId);
    }

    public ResponseEntity<Object> approvedBooking(long bookingId, long userId, boolean approved) {
        String path = API_PREFIX + "/" + bookingId + "?approved=" + approved;
        return patch(path, userId);
    }

    public ResponseEntity<Object> getBookingsByUserItem(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get(API_PREFIX + "/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class BookingClientSimpleTest {

    @Mock
    private RestTemplate restTemplate;

    private TestBookingClient bookingClient;

    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(5);

    @BeforeEach
    void setUp() {
        bookingClient = new TestBookingClient(restTemplate);
    }

    @Test
    @DisplayName("getBookings - базовый тест")
    void getBookings_basicTest() {
        // Given
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok().build());

        // When
        bookingClient.getBookings(1L, BookingState.ALL, 0, 10);

        // Then
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), anyMap());
    }


    @Test
    @DisplayName("getBookingsByUserItem - базовый тест")
    void getBookingsByUserItem_basicTest() {
        // Given
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok().build());

        // When
        bookingClient.getBookingsByUserItem(1L, BookingState.WAITING, 0, 10);

        // Then
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), anyMap());
    }
}