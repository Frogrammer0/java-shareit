import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemClientWrap itemClient;

    @BeforeEach
    void setUp() {
        itemClient = new ItemClientWrap(restTemplate);
    }

    private ItemDto createTestItemDto() {
        UserShortDto owner = UserShortDto.builder()
                .id(1L)
                .name("Owner Name")
                .build();

        return ItemDto.builder()
                .id(1L)
                .owner(owner)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(123L)
                .comments(List.of())
                .build();
    }

    private CommentDto createTestCommentDto() {
        ItemShortDto item = ItemShortDto.builder()
                .id(1L)
                .name("Test Item")
                .build();

        return CommentDto.builder()
                .id(1L)
                .authorName("Author Name")
                .item(item)
                .text("Test comment text")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("getItemsByUser - базовый тест")
    void getItemsByUser_basicTest() {

        when(restTemplate.exchange(
                eq("/items?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        )).thenReturn(ResponseEntity.ok().build());

        itemClient.getItemsByUser(1L, 0, 10);

        verify(restTemplate).exchange(
                eq("/items?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        );
    }



    @Test
    @DisplayName("search - базовый тест")
    void search_basicTest() {
        when(restTemplate.exchange(
                eq("/items/search?text=дрель"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        )).thenReturn(ResponseEntity.ok().build());

        itemClient.search("text=дрель", 1L, 0, 10);

        verify(restTemplate).exchange(
                eq("/items/search?text=дрель"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        );
    }

    @Test
    @DisplayName("getItemsByUser - с различными параметрами пагинации")
    void getItemsByUser_withDifferentPagination() {
        when(restTemplate.exchange(
                eq("/items?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        )).thenReturn(ResponseEntity.ok().build());

        itemClient.getItemsByUser(1L, 0, 10);
        verify(restTemplate).exchange(
                eq("/items?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        );

        reset(restTemplate);

    }



    @Test
    @DisplayName("search - с различными поисковыми запросами")
    void search_withDifferentQueries() {
        when(restTemplate.exchange(
                eq("/items/search?text=дрель"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        )).thenReturn(ResponseEntity.ok().build());

        itemClient.search("text=дрель", 1L, 0, 10);
        verify(restTemplate).exchange(
                eq("/items/search?text=дрель"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        );

        reset(restTemplate);

        when(restTemplate.exchange(
                eq("/items/search?text=молоток"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        )).thenReturn(ResponseEntity.ok().build());

        itemClient.search("text=молоток", 1L, 0, 10);
        verify(restTemplate).exchange(
                eq("/items/search?text=молоток"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        );
    }

}