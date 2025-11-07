import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

class TestableBaseClient extends BaseClient {
    public TestableBaseClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> testGet(String path) {
        return get(path);
    }

    public ResponseEntity<Object> testGet(String path, long userId) {
        return get(path, userId);
    }

    public ResponseEntity<Object> testGet(String path, Long userId, Map<String, Object> parameters) {
        return get(path, userId, parameters);
    }

    public <T> ResponseEntity<Object> testPost(String path, T body) {
        return post(path, body);
    }

    public <T> ResponseEntity<Object> testPost(String path, long userId, T body) {
        return post(path, userId, body);
    }

    public <T> ResponseEntity<Object> testPost(String path, Long userId, Map<String, Object> parameters, T body) {
        return post(path, userId, parameters, body);
    }

    public <T> ResponseEntity<Object> testPut(String path, long userId, T body) {
        return put(path, userId, body);
    }

    public <T> ResponseEntity<Object> testPut(String path, long userId, Map<String, Object> parameters, T body) {
        return put(path, userId, parameters, body);
    }

    public <T> ResponseEntity<Object> testPatch(String path, T body) {
        return patch(path, body);
    }

    public <T> ResponseEntity<Object> testPatch(String path, long userId) {
        return patch(path, userId);
    }

    public <T> ResponseEntity<Object> testPatch(String path, long userId, T body) {
        return patch(path, userId, body);
    }

    public <T> ResponseEntity<Object> testPatch(String path, Long userId, Map<String, Object> parameters, T body) {
        return patch(path, userId, parameters, body);
    }

    public ResponseEntity<Object> testDelete(String path) {
        return delete(path);
    }

    public ResponseEntity<Object> testDelete(String path, long userId) {
        return delete(path, userId);
    }

    public ResponseEntity<Object> testDelete(String path, Long userId, Map<String, Object> parameters) {
        return delete(path, userId, parameters);
    }
}