import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingState;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class BookingStateTest {

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void from_WhenValidStateName_ShouldReturnState(BookingState expectedState) {

        Optional<BookingState> result = BookingState.from(expectedState.name());

        assertTrue(result.isPresent());
        assertEquals(expectedState, result.get());
    }

    @Test
    void from_WhenValidStateNameIgnoreCase_ShouldReturnState() {
        Optional<BookingState> result = BookingState.from("all");

        assertTrue(result.isPresent());
        assertEquals(BookingState.ALL, result.get());
    }

    @Test
    void from_WhenInvalidStateName_ShouldReturnEmpty() {
        Optional<BookingState> result = BookingState.from("INVALID_STATE");

        assertFalse(result.isPresent());
    }

    @Test
    void from_WhenNullStateName_ShouldReturnEmpty() {
        Optional<BookingState> result = BookingState.from(null);

        assertFalse(result.isPresent());
    }
}