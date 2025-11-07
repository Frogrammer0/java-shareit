import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.booking.BookingClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingClientConstructorTest {

    @Test
    void testConstructor() {
        BookingClient client = new BookingClient("http://localhost:9090", new RestTemplateBuilder());
        assertNotNull(client);
    }




}