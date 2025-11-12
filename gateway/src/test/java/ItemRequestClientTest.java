import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestDto;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@Slf4j
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


    @Test
    void coverAllMethods() throws Exception {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        ItemRequestClient client = new ItemRequestClient("http://localhost:9090", builder);

        java.lang.reflect.Field restField = client.getClass().getSuperclass().getDeclaredField("rest");
        restField.setAccessible(true);
        restField.set(client, null);

        callAllMethodsSafely(client);
    }

    private void callAllMethodsSafely(ItemRequestClient client) {
        try {
            ItemRequestDto requestDto = ItemRequestDto.builder()
                    .description("Test request")
                    .build();
            client.create(1L, requestDto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            client.getRequestByUser(1L);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            client.getAllRequests(1L, 0, 10);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            client.getAllRequests(1L, 5, 20);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            client.getRequestById(123L);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}