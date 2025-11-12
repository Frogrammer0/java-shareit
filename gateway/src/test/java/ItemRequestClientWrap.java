import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.ItemRequestDto;

import java.util.Map;

class ItemRequestClientWrap extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClientWrap(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(long userId, ItemRequestDto itemRequestDto) {
        return post(API_PREFIX, userId, itemRequestDto);
    }

    public ResponseEntity<Object> getRequestByUser(long xUserId) {
        return get(API_PREFIX, xUserId);
    }

    public ResponseEntity<Object> getAllRequests(long xUserId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get(API_PREFIX + "/all?from={from}&size={size}", xUserId, parameters);
    }

    public ResponseEntity<Object> getRequestById(long requestId) {
        return get(API_PREFIX + "/" + requestId);
    }
}