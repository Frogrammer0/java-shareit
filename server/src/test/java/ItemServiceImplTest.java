
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserShortDto;
import ru.practicum.shareit.user.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemValidator itemValidator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User createTestUser(Long id) {
        return User.builder()
                .id(id)
                .name("Test User " + id)
                .email("test" + id + "@email.com")
                .build();
    }

    private Item createTestItem(Long id, User owner) {
        return Item.builder()
                .id(id)
                .name("Test Item " + id)
                .description("Test Description " + id)
                .available(true)
                .owner(owner)
                .build();
    }

    private ItemDto createTestItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(new UserShortDto(1L, "Test User"))
                .comments(List.of())
                .build();
    }

    private CommentDto createTestCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("Test comment")
                .authorName("Test Author")
                .created(LocalDateTime.now())
                .build();
    }

    private Comment createTestComment(Long id, Item item, User author) {
        return Comment.builder()
                .id(id)
                .text("Test comment")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    private Booking createTestBooking(Long id, Item item, User booker, LocalDateTime start, LocalDateTime end) {
        return Booking.builder()
                .id(id)
                .item(item)
                .booker(booker)
                .start(start)
                .end(end)
                .status(Status.APPROVED)
                .build();
    }

    private BookingShortDto createTestBookingShortDto(Long id, Long bookerId) {
        return BookingShortDto.builder()
                .id(id)
                .bookerId(bookerId)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
    }



    @Test
    void getAllItemsByUser_WhenUserNotExists_ShouldThrowException() {

        Long userId = 999L;

        doThrow(new NotFoundException("User not found"))
                .when(userValidator).isUserExists(userId);

        assertThrows(NotFoundException.class, () ->
                itemService.getAllItemsByUser(userId, 0, 10));

        verify(userValidator).isUserExists(userId);
        verify(itemRepository, never()).findAllByOwnerId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getItemById_WhenItemExists_ShouldReturnItemWithComments() {

        Long itemId = 1L;
        User owner = createTestUser(1L);
        Item item = createTestItem(itemId, owner);
        Comment comment = createTestComment(1L, item, createTestUser(2L));
        List<Comment> comments = List.of(comment);
        CommentDto commentDto = createTestCommentDto();
        ItemDto expectedDto = createTestItemDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);
        when(itemMapper.toItemDto(item, List.of(commentDto))).thenReturn(expectedDto);


        ItemDto result = itemService.getItemById(itemId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(itemRepository).findById(itemId);
        verify(commentRepository).findAllByItemId(itemId);
        verify(commentMapper).toCommentDto(comment);
    }

    @Test
    void create_WhenValidData_ShouldCreateItem() {
        // Arrange
        Long userId = 1L;
        User user = createTestUser(userId);
        ItemDto itemDto = ItemDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("New Item")
                .description("New Description")
                .available(true)
                .owner(user)
                .build();
        ItemDto expectedDto = createTestItemDto();


        doNothing().when(itemValidator).validate(itemDto);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.toItem(itemDto, user)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of());
        when(itemMapper.toItemDto(item, List.of())).thenReturn(expectedDto);


        ItemDto result = itemService.create(itemDto, userId);


        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(itemValidator).validate(itemDto);
        verify(userRepository).findById(userId);
        verify(itemRepository).save(item);
    }

    @Test
    void create_WhenWithRequestId_ShouldSetRequest() {

        Long userId = 1L;
        Long requestId = 1L;
        User user = createTestUser(userId);
        ItemRequest request = ItemRequest.builder().id(requestId).build();
        ItemDto itemDto = ItemDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .requestId(requestId)
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("New Item")
                .description("New Description")
                .available(true)
                .owner(user)
                .request(request)
                .build();
        ItemDto expectedDto = createTestItemDto();


        doNothing().when(itemValidator).validate(itemDto);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.toItem(itemDto, user)).thenReturn(item);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(item)).thenReturn(item);
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of());
        when(itemMapper.toItemDto(item, List.of())).thenReturn(expectedDto);


        ItemDto result = itemService.create(itemDto, userId);


        assertNotNull(result);
        assertEquals(request, item.getRequest());
        verify(requestRepository).findById(requestId);
    }

    @Test
    void delete_WhenValidData_ShouldDeleteItem() {
        Long userId = 1L;
        Long itemId = 1L;

        doNothing().when(itemValidator).hasAccess(itemId, userId);
        doNothing().when(itemValidator).isItemExists(itemId);
        doNothing().when(userValidator).isUserExists(userId);

        itemService.delete(itemId, userId);

        verify(itemValidator).hasAccess(itemId, userId);
        verify(itemValidator).isItemExists(itemId);
        verify(userValidator).isUserExists(userId);
        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void delete_WhenUserNotOwner_ShouldThrowForbiddenException() {
        Long userId = 1L;
        Long itemId = 1L;

        doThrow(new ForbiddenException("No access"))
                .when(itemValidator).hasAccess(itemId, userId);

        assertThrows(ForbiddenException.class, () -> itemService.delete(itemId, userId));
        verify(itemValidator).hasAccess(itemId, userId);
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void edit_WhenValidData_ShouldUpdateItem() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = createTestUser(userId);
        ItemDto itemDto = ItemDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .owner(user)
                .build();
        ItemDto expectedDto = createTestItemDto();

        doNothing().when(itemValidator).isItemExists(itemId);
        doNothing().when(itemValidator).hasAccess(itemId, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.toItem(itemDto, user)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of());
        when(itemMapper.toItemDto(item, List.of())).thenReturn(expectedDto);

        ItemDto result = itemService.edit(itemDto, userId, itemId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(itemValidator).isItemExists(itemId);
        verify(itemValidator).hasAccess(itemId, userId);
        verify(itemRepository).save(item);
    }


    @Test
    void search_WhenQueryIsBlank_ShouldReturnEmptyList() {
        String blankQuery = "   ";

        List<ItemDto> result = itemService.search(blankQuery, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchAvailableItems(anyString(), anyInt(), anyInt());
    }

    @Test
    void postComment_WhenValidData_ShouldCreateComment() {
        Long itemId = 1L;
        Long userId = 1L;
        User author = createTestUser(userId);
        Item item = createTestItem(itemId, createTestUser(2L));
        CommentDto commentDto = CommentDto.builder()
                .text("Test comment")
                .build();
        Comment comment = createTestComment(1L, item, author);
        CommentDto expectedDto = createTestCommentDto();

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(userId))
                .thenReturn(List.of(createTestBooking(1L, item, author,
                        LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1))));

        doNothing().when(itemValidator).validateUsed(itemId, userId);
        doNothing().when(itemValidator).validateComment(commentDto);

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentMapper.toComment(commentDto, item, author)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(expectedDto);

        CommentDto result = itemService.postComment(itemId, userId, commentDto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(itemValidator).validateUsed(itemId, userId);
        verify(itemValidator).validateComment(commentDto);
        verify(commentRepository).save(comment);
        assertNotNull(commentDto.getCreated()); // created date should be set
    }

    @Test
    void postComment_WhenUserNeverBookedItem_ShouldThrowValidationException() {
        // Arrange
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto commentDto = createTestCommentDto();

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(userId)).thenReturn(List.of());

        // Исправлено: doThrow() для void метода
        doThrow(new ValidationException("User never booked this item"))
                .when(itemValidator).validateUsed(itemId, userId);

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                itemService.postComment(itemId, userId, commentDto));

        verify(itemValidator).validateUsed(itemId, userId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void create_WhenRequestNotExists_ShouldThrowNotFoundException() {
        // Arrange
        Long userId = 1L;
        Long requestId = 999L;
        User user = createTestUser(userId);
        ItemDto itemDto = ItemDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .requestId(requestId)
                .build();

        // Исправлено: doNothing() для void метода
        doNothing().when(itemValidator).validate(itemDto);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.toItem(itemDto, user)).thenReturn(createTestItem(1L, user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, userId));
        verify(requestRepository).findById(requestId);
        verify(itemRepository, never()).save(any(Item.class));
    }

}