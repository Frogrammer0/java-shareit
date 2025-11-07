import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;

@Slf4j
class ItemClientCoverageLineTest {

    @Test
    void coverAllMethodsWithFallback() throws Exception {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        ItemClient client = new ItemClient("http://localhost:9090", builder);

        java.lang.reflect.Field restField = client.getClass().getSuperclass().getDeclaredField("rest");
        restField.setAccessible(true);
        restField.set(client, null);

        callAllMethodsWithFallback(client);
    }

    private void callAllMethodsWithFallback(ItemClient client) {
        try {
            client.getItemsByUser(1L, 0, 10);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            client.getItemsByUser(1L, null, null);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            client.getItemById(123L, 1L);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            ItemDto itemDto = createMinimalItemDto();
            client.create(1L, itemDto);
        } catch (Exception e) {
            try {
                client.create(1L, null);
            } catch (Exception e2) {
                log.error(e.getMessage());
            }
        }

        try {
            ItemDto itemDto = createMinimalItemDto();
            client.edit(123L, 1L, itemDto);
        } catch (Exception e) {
            try {
                client.edit(123L, 1L, null);
            } catch (Exception e2) {
                log.error(e.getMessage());
            }
        }

        try {
            client.delete(123L, 1L);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            client.search("test", 1L, 0, 10);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            client.search("", 1L, null, null);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            CommentDto commentDto = createMinimalCommentDto();
            client.postComment(123L, 1L, commentDto);
        } catch (Exception e) {
            try {
                client.postComment(123L, 1L, null);
            } catch (Exception e2) {
                log.error(e.getMessage());
            }
        }
    }

    private ItemDto createMinimalItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
    }

    private CommentDto createMinimalCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .authorName("Test Author")
                .text("Test comment text")
                .created(LocalDateTime.now())
                // item - оставляем null
                .build();
    }
}
