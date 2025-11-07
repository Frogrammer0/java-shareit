import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemValidatorTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    private ItemValidator itemValidator;

    @BeforeEach
    void setUp() {
        itemValidator = new ItemValidator(itemRepository, bookingRepository);
    }

    @Test
    void validate_WhenItemDtoIsValid_ShouldNotThrowException() {
        ItemDto itemDto = ItemDto.builder()
                .name("Valid Item")
                .description("Valid Description")
                .available(true)
                .build();

        assertDoesNotThrow(() -> itemValidator.validate(itemDto));
    }

    @Test
    void validateName_WhenNameIsValid_ShouldNotThrowException() {
        String validName = "Valid Name";

        assertDoesNotThrow(() -> itemValidator.validateName(validName));
    }

    @Test
    void validateName_WhenNameIsNull_ShouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validateName(null));
        assertEquals("не указано название", exception.getMessage());
    }

    @Test
    void validateName_WhenNameIsBlank_ShouldThrowValidationException() {
        String blankName = "   ";

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validateName(blankName));
        assertEquals("не указано название", exception.getMessage());
    }

    @Test
    void validateName_WhenNameIsEmpty_ShouldThrowValidationException() {
        String emptyName = "";

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validateName(emptyName));
        assertEquals("не указано название", exception.getMessage());
    }

    @Test
    void validateAvailable_WhenAvailableIsTrue_ShouldNotThrowException() {
        assertDoesNotThrow(() -> itemValidator.validateAvailable(true));
    }

    @Test
    void validateAvailable_WhenAvailableIsFalse_ShouldNotThrowException() {
        assertDoesNotThrow(() -> itemValidator.validateAvailable(false));
    }

    @Test
    void validateAvailable_WhenAvailableIsNull_ShouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validateAvailable(null));
        assertEquals("не указан статус вещи", exception.getMessage());
    }

    @Test
    void validateDescription_WhenDescriptionIsValid_ShouldNotThrowException() {
        String validDescription = "Valid Description";

        assertDoesNotThrow(() -> itemValidator.validateDescription(validDescription));
    }

    @Test
    void validateDescription_WhenDescriptionIsNull_ShouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validateDescription(null));
        assertEquals("не указано описание", exception.getMessage());
    }

    @Test
    void validateDescription_WhenDescriptionIsBlank_ShouldThrowValidationException() {
        String blankDescription = "   ";

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validateDescription(blankDescription));
        assertEquals("не указано описание", exception.getMessage());
    }

    @Test
    void validateDescription_WhenDescriptionIsEmpty_ShouldThrowValidationException() {
        String emptyDescription = "";

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validateDescription(emptyDescription));
        assertEquals("не указано описание", exception.getMessage());
    }


    @Test
    void validateComment_WhenCommentIsValid_ShouldNotThrowException() {
        CommentDto commentDto = CommentDto.builder()
                .text("Valid comment text")
                .build();

        assertDoesNotThrow(() -> itemValidator.validateComment(commentDto));
    }

    @Test
    void validateComment_WhenCommentTextIsNull_ShouldThrowValidationException() {
        CommentDto commentDto = CommentDto.builder()
                .text(null)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validateComment(commentDto));
        assertEquals("комментарий не должен быть пуст", exception.getMessage());
    }

    @Test
    void validateComment_WhenCommentTextIsBlank_ShouldThrowValidationException() {
        CommentDto commentDto = CommentDto.builder()
                .text("   ")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validateComment(commentDto));
        assertEquals("комментарий не должен быть пуст", exception.getMessage());
    }

    @Test
    void validateComment_WhenCommentTextIsEmpty_ShouldThrowValidationException() {
        CommentDto commentDto = CommentDto.builder()
                .text("")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validateComment(commentDto));
        assertEquals("комментарий не должен быть пуст", exception.getMessage());
    }

    @Test
    void hasAccess_WhenUserHasAccess_ShouldNotThrowException() {
        long itemId = 1L;
        long userId = 1L;
        when(itemRepository.existsByIdAndOwnerId(itemId, userId)).thenReturn(true);

        assertDoesNotThrow(() -> itemValidator.hasAccess(itemId, userId));
        verify(itemRepository).existsByIdAndOwnerId(itemId, userId);
    }

    @Test
    void hasAccess_WhenUserHasNoAccess_ShouldThrowForbiddenException() {
        long itemId = 1L;
        long userId = 1L;
        when(itemRepository.existsByIdAndOwnerId(itemId, userId)).thenReturn(false);

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> itemValidator.hasAccess(itemId, userId));
        assertEquals("отсутствие прав доступа на изменения ресурса", exception.getMessage());
        verify(itemRepository).existsByIdAndOwnerId(itemId, userId);
    }

    @Test
    void isItemExists_WhenItemExists_ShouldNotThrowException() {
        long itemId = 1L;
        when(itemRepository.existsById(itemId)).thenReturn(true);

        assertDoesNotThrow(() -> itemValidator.isItemExists(itemId));
        verify(itemRepository).existsById(itemId);
    }

    @Test
    void isItemExists_WhenItemDoesNotExist_ShouldThrowNotFoundException() {
        long itemId = 1L;
        when(itemRepository.existsById(itemId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemValidator.isItemExists(itemId));
        assertEquals("Вещь с id = 1 не найдена", exception.getMessage());
        verify(itemRepository).existsById(itemId);
    }

    @Test
    void validate_WhenNameIsInvalid_ShouldThrowValidationException() {
        ItemDto itemDto = ItemDto.builder()
                .name(null)
                .description("Valid Description")
                .available(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validate(itemDto));
        assertEquals("не указано название", exception.getMessage());
    }

    @Test
    void validate_WhenDescriptionIsInvalid_ShouldThrowValidationException() {
        ItemDto itemDto = ItemDto.builder()
                .name("Valid Name")
                .description(null)
                .available(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validate(itemDto));
        assertEquals("не указано описание", exception.getMessage());
    }

    @Test
    void validate_WhenAvailableIsInvalid_ShouldThrowValidationException() {
        ItemDto itemDto = ItemDto.builder()
                .name("Valid Name")
                .description("Valid Description")
                .available(null)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemValidator.validate(itemDto));
        assertEquals("не указан статус вещи", exception.getMessage());
    }
}