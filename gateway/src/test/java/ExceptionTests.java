
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTests {

    @Test
    void testMessageDuplicatedData() {
        String message = "Duplicate entry";
        DuplicatedDataException exception = new DuplicatedDataException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testMessageForbidden() {
        String message = "Access denied";
        ForbiddenException exception = new ForbiddenException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testMessageValidation() {
        String message = "Invalid data";
        ValidationException exception = new ValidationException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testMessageNotFound() {
        String message = "Object not found";
        NotFoundException exception = new NotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }


}
