import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class ItemMapperTest {

    @InjectMocks
    private ItemMapper itemMapper;

    @Test
    void toItemDto_WhenValidItem_ShouldMapCorrectly() {

        User owner = User.builder()
                .id(1L)
                .name("Test Owner")
                .email("owner@email.com")
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(5L)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Test comment")
                .authorName("Comment Author")
                .created(LocalDateTime.now())
                .build();

        List<CommentDto> comments = List.of(commentDto);


        ItemDto result = itemMapper.toItemDto(item, comments);


        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(5L, result.getRequestId());
        assertNotNull(result.getOwner());
        assertEquals(1L, result.getOwner().getId());
        assertEquals("Test Owner", result.getOwner().getName());
        assertEquals(1, result.getComments().size());
        assertEquals("Test comment", result.getComments().getFirst().getText());
    }

    @Test
    void toItemDto_WhenRequestIsNull_ShouldSetRequestIdNull() {

        User owner = User.builder()
                .id(1L)
                .name("Test Owner")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .request(null)
                .build();


        ItemDto result = itemMapper.toItemDto(item, List.of());


        assertNotNull(result);
        assertNull(result.getRequestId());
    }

    @Test
    void toItem_WhenValidItemDto_ShouldMapCorrectly() {

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();


        Item result = itemMapper.toItem(itemDto, user);


        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(user, result.getOwner());
    }

    @Test
    void toItemShortDto_WhenValidItem_ShouldMapCorrectly() {

        User owner = User.builder()
                .id(1L)
                .name("Test Owner")
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(5L)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .owner(owner)
                .request(request)
                .build();

        ItemShortDto result = itemMapper.toItemShortDto(item);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals(5L, result.getRequestId());
        assertEquals(1L, result.getOwnerId());
    }

    @Test
    void toItemShortDto_WhenRequestIsNull_ShouldSetRequestIdNull() {
        User owner = User.builder()
                .id(1L)
                .name("Test Owner")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .owner(owner)
                .request(null)
                .build();

        ItemShortDto result = itemMapper.toItemShortDto(item);

        assertNotNull(result);
        assertNull(result.getRequestId());
    }
}