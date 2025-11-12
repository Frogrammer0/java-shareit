import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingDtoTest {

    @Test
    void allDtoClasses_ShouldHaveWorkingBuilders() {
        assertNotNull(BookingDto.builder().build());
        assertNotNull(BookingShortDto.builder().build());
        assertNotNull(BookingResponseDto.builder().build());
        assertNotNull(BookingRequestDto.builder().build());
    }
}