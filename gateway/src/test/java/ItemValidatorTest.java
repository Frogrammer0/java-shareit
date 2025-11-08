import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemValidator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemValidatorTest {

    @InjectMocks
    private ItemValidator itemValidator;

    @Test
    void validate_WhenValidItemDto_ShouldNotThrowException() {
        ItemDto validItemDto = new ItemDto();
        validItemDto.setName("Valid Name");
        validItemDto.setDescription("Valid Description");
        validItemDto.setAvailable(true);

        assertDoesNotThrow(() -> itemValidator.validate(validItemDto));
    }

    @Test
    void validateName_WithVariousInvalidValues_ShouldThrowException() {
        assertAll(
                () -> assertThrows(ValidationException.class, () -> itemValidator.validateName(null)),
                () -> assertThrows(ValidationException.class, () -> itemValidator.validateName("")),
                () -> assertThrows(ValidationException.class, () -> itemValidator.validateName("   "))
        );
    }

    @Test
    void validateName_WhenValid_ShouldNotThrowException() {
        assertDoesNotThrow(() -> itemValidator.validateName("Valid Name"));
    }

    @Test
    void validateAvailable_WhenNull_ShouldThrowException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validateAvailable(null));
        assertEquals("не указан статус вещи", exception.getMessage());
    }

    @Test
    void validateAvailable_WhenValid_ShouldNotThrowException() {
        assertAll(
                () -> assertDoesNotThrow(() -> itemValidator.validateAvailable(true)),
                () -> assertDoesNotThrow(() -> itemValidator.validateAvailable(false))
        );
    }

    @Test
    void validateDescription_WithVariousInvalidValues_ShouldThrowException() {
        assertAll(
                () -> assertThrows(ValidationException.class, () -> itemValidator.validateDescription(null)),
                () -> assertThrows(ValidationException.class, () -> itemValidator.validateDescription("")),
                () -> assertThrows(ValidationException.class, () -> itemValidator.validateDescription("   "))
        );
    }


    @Test
    void validateComment_WithVariousInvalidValues_ShouldThrowException() {
        CommentDto commentDto = new CommentDto();

        assertAll(
                () -> {
                    commentDto.setText(null);
                    assertThrows(ValidationException.class, () -> itemValidator.validateComment(commentDto));
                },
                () -> {
                    commentDto.setText("");
                    assertThrows(ValidationException.class, () -> itemValidator.validateComment(commentDto));
                },
                () -> {
                    commentDto.setText("   ");
                    assertThrows(ValidationException.class, () -> itemValidator.validateComment(commentDto));
                }
        );
    }

    @Test
    void validateItemDto_WithVariousInvalidFields_ShouldThrowException() {
        assertAll(
                () -> {
                    ItemDto dto = createValidItemDto();
                    dto.setName(null);
                    assertThrows(ValidationException.class, () -> itemValidator.validate(dto));
                },
                () -> {
                    ItemDto dto = createValidItemDto();
                    dto.setDescription(null);
                    assertThrows(ValidationException.class, () -> itemValidator.validate(dto));
                },
                () -> {
                    ItemDto dto = createValidItemDto();
                    dto.setAvailable(null);
                    assertThrows(ValidationException.class, () -> itemValidator.validate(dto));
                }
        );
    }

    private ItemDto createValidItemDto() {
        ItemDto dto = new ItemDto();
        dto.setName("Valid Name");
        dto.setDescription("Valid Description");
        dto.setAvailable(true);
        return dto;
    }
}