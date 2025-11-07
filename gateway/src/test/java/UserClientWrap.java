import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.UserDto;

import java.util.Map;

class UserClientWrap extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClientWrap(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> getAllUsers(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get(API_PREFIX + "?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getUserById(long userId) {
        return get(API_PREFIX + "/" + userId);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post(API_PREFIX, userDto);
    }

    public ResponseEntity<Object> edit(long userId, UserDto userDto) {
        return patch(API_PREFIX + "/" + userId, userDto);
    }

    public ResponseEntity<Object> delete(long userId) {
        return delete(API_PREFIX + "/" + userId);
    }
}