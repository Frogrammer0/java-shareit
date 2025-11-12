
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Status;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void testEnumValues() {
        Status[] statuses = Status.values();
        assertEquals(3, statuses.length);
        assertArrayEquals(new Status[]{Status.WAITING, Status.APPROVED, Status.REJECTED}, statuses);
    }

    @Test
    void testValueOf() {
        assertEquals(Status.WAITING, Status.valueOf("WAITING"));
        assertEquals(Status.APPROVED, Status.valueOf("APPROVED"));
        assertEquals(Status.REJECTED, Status.valueOf("REJECTED"));
    }
}
