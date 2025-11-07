import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemRequestClientWrap itemRequestClient;

    @BeforeEach
    void setUp() {
        itemRequestClient = new ItemRequestClientWrap(restTemplate);
    }



    @Test
    @DisplayName("getAllRequests - базовый тест")
    void getAllRequests_basicTest() {
        when(restTemplate.exchange(
                eq("/requests/all?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        )).thenReturn(ResponseEntity.ok().build());

        itemRequestClient.getAllRequests(1L, 0, 10);

        verify(restTemplate).exchange(
                eq("/requests/all?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        );
    }


    @Test
    @DisplayName("getAllRequests - с различными параметрами пагинации")
    void getAllRequests_withDifferentPagination() {

        when(restTemplate.exchange(
                eq("/requests/all?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        )).thenReturn(ResponseEntity.ok().build());

        itemRequestClient.getAllRequests(1L, 0, 10);
        verify(restTemplate).exchange(
                eq("/requests/all?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        );
        reset(restTemplate);
        when(restTemplate.exchange(
                eq("/requests/all?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 100, "size", 5))
        )).thenReturn(ResponseEntity.ok().build());

        itemRequestClient.getAllRequests(1L, 100, 5);
        verify(restTemplate).exchange(
                eq("/requests/all?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 100, "size", 5))
        );
    }


    @Test
    @DisplayName("getAllRequests - с разными пользователями и пагинацией")
    void getAllRequests_withDifferentUsersAndPagination() {
        when(restTemplate.exchange(
                eq("/requests/all?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                any(Map.class)
        )).thenReturn(ResponseEntity.ok().build());

        itemRequestClient.getAllRequests(1L, 0, 10);
        verify(restTemplate).exchange(
                eq("/requests/all?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 0, "size", 10))
        );

        reset(restTemplate);
        when(restTemplate.exchange(
                eq("/requests/all?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                any(Map.class)
        )).thenReturn(ResponseEntity.ok().build());

        itemRequestClient.getAllRequests(999L, 50, 20);
        verify(restTemplate).exchange(
                eq("/requests/all?from={from}&size={size}"),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(Map.of("from", 50, "size", 20))
        );
    }
}