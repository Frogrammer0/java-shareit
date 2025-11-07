
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.UserDto;
import java.util.Map;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    private UserClientWrap userClient;

    @BeforeEach
    void setUp() {
        userClient = new UserClientWrap(restTemplate);
    }

    private UserDto createTestUserDto() {
        return UserDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();
    }

    @Test
    @DisplayName("getAllUsers - базовый тест")
    void getAllUsers_basicTest() {
        doReturn(ResponseEntity.ok().build())
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(), any(Class.class), any(Map.class));

        userClient.getAllUsers(1L, 0, 10);

        verify(restTemplate).exchange(
                eq("/users?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        );
    }


    @Test
    @DisplayName("getAllUsers - с различными параметрами пагинации")
    void getAllUsers_withDifferentPagination() {
        doReturn(ResponseEntity.ok().build())
                .when(restTemplate).exchange(anyString(), any(HttpMethod.class), any(), any(Class.class), any(Map.class));

        userClient.getAllUsers(1L, 0, 10);
        verify(restTemplate).exchange(
                eq("/users?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        );

        userClient.getAllUsers(1L, 10, 5);
        verify(restTemplate).exchange(
                eq("/users?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 10, "size", 5))
        );

        userClient.getAllUsers(1L, 100, 20);
        verify(restTemplate).exchange(
                eq("/users?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 100, "size", 20))
        );
    }

}