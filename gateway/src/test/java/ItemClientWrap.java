import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;

import java.util.Map;

class ItemClientWrap extends BaseClient {
        private static final String API_PREFIX = "/items";

        public ItemClientWrap(RestTemplate restTemplate) {
            super(restTemplate);
        }

        public ResponseEntity<Object> getItemsByUser(long userId, Integer from, Integer size) {
            Map<String, Object> parameters = Map.of(
                    "from", from,
                    "size", size
            );
            return get(API_PREFIX + "?from={from}&size={size}", userId, parameters);
        }

        public ResponseEntity<Object> getItemById(long itemId, long userId) {
            return get(API_PREFIX + "/" + itemId, userId);
        }

        public ResponseEntity<Object> create(long userId, ItemDto itemDto) {
            return post(API_PREFIX, userId, itemDto);
        }

        public ResponseEntity<Object> edit(long itemId, long userId, ItemDto itemDto) {
            return patch(API_PREFIX + "/" + itemId, userId, itemDto);
        }

        public ResponseEntity<Object> delete(long itemId, long userId) {
            return delete(API_PREFIX + "/" + itemId, userId);
        }

        public ResponseEntity<Object> search(String text, Long userId, Integer from, Integer size) {
            Map<String, Object> parameters = Map.of(
                    "from", from,
                    "size", size
            );
            return get(API_PREFIX + "/search?" + text, userId, parameters);
        }

        public ResponseEntity<Object> postComment(long itemId, long userId, CommentDto commentDto) {
            String path = API_PREFIX + "/" + itemId + "/comment";
            return post(path, userId, commentDto);
        }
    }