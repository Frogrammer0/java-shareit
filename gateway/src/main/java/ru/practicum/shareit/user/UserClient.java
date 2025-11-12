package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Slf4j
@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getAllUsers(long userId, Integer from, Integer size) {
        log.info("get all users in UserClient");
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> getUserById(long userId) {
        log.info("get User By Id in UserClient");
        return get("/" + userId);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        log.info("create user in UserClient");
        return post("", userDto);
    }

    public ResponseEntity<Object> edit(long userId, UserDto userDto) {
        log.info("edit user in UserClient");
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> delete(long userId) {
        log.info("delete user in UserClient");
        return delete("/" + userId);
    }
}
