
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidator;

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
    private CommentMapper commentMapper;

    @Mock
    private ItemValidator itemValidator;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getAllItemsByUser_ShouldReturnItems() {
        long userId = 1L;
        Item item = Item.builder().id(1L).build();
        ItemDto itemDto = ItemDto.builder().id(1L).build();

        userValidator.isUserExists(userId);
        when(itemRepository.findAllByOwnerId(userId, 0, 10)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdInAndStatusOrderByStartAsc(anyList(), any())).thenReturn(List.of());
        when(commentRepository.findAllByItemIdIn(anyList())).thenReturn(List.of());
        when(itemMapper.toItemDto(any(), any())).thenReturn(itemDto);

        List<ItemDto> result = itemService.getAllItemsByUser(userId, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getItemById_ShouldReturnItem() {
        long itemId = 1L;
        Item item = Item.builder().id(itemId).build();
        ItemDto itemDto = ItemDto.builder().id(itemId).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of());
        when(itemMapper.toItemDto(item, List.of())).thenReturn(itemDto);

        ItemDto result = itemService.getItemById(itemId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        verify(itemRepository).findById(itemId);
    }

    @Test
    void getItemById_WhenNotFound_ShouldThrowException() {
        long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId));
    }

    @Test
    void create_ShouldCreateItem() {
        long userId = 1L;
        ItemDto itemDto = ItemDto.builder().name("Item").description("Desc").available(true).build();
        User user = User.builder().id(userId).build();
        Item item = Item.builder().id(1L).build();
        ItemDto savedDto = ItemDto.builder().id(1L).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.toItem(itemDto, user)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of());
        when(itemMapper.toItemDto(item, List.of())).thenReturn(savedDto);

        ItemDto result = itemService.create(itemDto, userId);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(itemRepository).save(item);
    }

    @Test
    void create_WhenUserNotFound_ShouldThrowException() {
        long userId = 1L;
        ItemDto itemDto = ItemDto.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, userId));
    }

    @Test
    void delete_ShouldDeleteItem() {
        long itemId = 1L;
        long userId = 1L;

        doNothing().when(itemValidator).hasAccess(itemId, userId);
        doNothing().when(itemValidator).isItemExists(itemId);
        doNothing().when(userValidator).isUserExists(userId);

        itemService.delete(itemId, userId);

        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void edit_ShouldEditItem() {
        long itemId = 1L;
        long userId = 1L;
        ItemDto itemDto = ItemDto.builder().name("Updated").build();
        User user = User.builder().id(userId).build();
        Item item = Item.builder().id(itemId).build();
        ItemDto updatedDto = ItemDto.builder().id(itemId).build();

        doNothing().when(itemValidator).isItemExists(itemId);
        doNothing().when(itemValidator).hasAccess(itemId, userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.toItem(itemDto, user)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of());
        when(itemMapper.toItemDto(item, List.of())).thenReturn(updatedDto);

        ItemDto result = itemService.edit(itemDto, userId, itemId);

        assertNotNull(result);
        verify(itemValidator).isItemExists(itemId);
        verify(itemValidator).hasAccess(itemId, userId);
        verify(itemRepository).save(item);
    }

    @Test
    void search_WithQuery_ShouldReturnItems() {
        String query = "test";
        Item item = Item.builder().id(1L).build();
        ItemDto itemDto = ItemDto.builder().id(1L).build();

        when(itemRepository.searchAvailableItems(query, 0, 10)).thenReturn(List.of(item));
        when(commentRepository.findAllByItemIdIn(anyList())).thenReturn(List.of());
        when(itemMapper.toItemDto(any(), any())).thenReturn(itemDto);

        List<ItemDto> result = itemService.search(query, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRepository).searchAvailableItems(query, 0, 10);
    }

    @Test
    void search_WithBlankQuery_ShouldReturnEmptyList() {
        List<ItemDto> result = itemService.search("   ", 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void postComment_ShouldPostComment() {
        long itemId = 1L;
        long userId = 1L;
        CommentDto commentDto = CommentDto.builder().text("Comment").build();
        User user = User.builder().id(userId).build();
        Item item = Item.builder().id(itemId).build();
        Comment comment = Comment.builder().id(1L).build();
        CommentDto savedDto = CommentDto.builder().id(1L).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentMapper.toComment(commentDto, item, user)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(savedDto);

        CommentDto result = itemService.postComment(itemId, userId, commentDto);

        assertNotNull(result);
        assertEquals(savedDto, result); // Добавил проверку результата
        verify(itemValidator).validateUsed(itemId, userId);
        verify(commentRepository).save(comment);
        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
    }

}