import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    @Test
    void toItemRequestDto_ShouldMapCorrectly() {
        User requestor = User.builder().id(1L).name("User").build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Need item")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        List<ItemShortDto> items = List.of(ItemShortDto.builder().id(1L).name("Item").build());

        ItemRequestDto result = itemRequestMapper.toItemRequestDto(itemRequest, items);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Need item", result.getDescription());
        assertNotNull(result.getRequestor());
        assertEquals(1L, result.getRequestor().getId());
        assertEquals("User", result.getRequestor().getName());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void toItemRequest_ShouldMapCorrectly() {
        User user = User.builder().id(1L).name("User").build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need item")
                .created(LocalDateTime.now())
                .build();

        ItemRequest result = itemRequestMapper.toItemRequest(itemRequestDto, user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Need item", result.getDescription());
        assertEquals(user, result.getRequestor());
    }
}