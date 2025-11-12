import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

class BookingClientTestRef extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClientTestRef(RestTemplate restTemplate) {
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