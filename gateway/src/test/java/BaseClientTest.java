import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    private TestableBaseClient baseClient;

    @BeforeEach
    void setUp() {
        baseClient = new TestableBaseClient(restTemplate);
    }

    @Test
    @DisplayName("GET без параметров и userId")
    void get_WithoutParametersAndUserId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");
        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.GET), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testGet("/test");

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    @DisplayName("GET с userId")
    void get_WithUserId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");
        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.GET), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testGet("/test", 1L);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    @DisplayName("GET с userId и параметрами")
    void get_WithUserIdAndParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");
        Map<String, Object> parameters = Map.of("param1", "value1", "param2", "value2");

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.GET), any(), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testGet("/test", 1L, parameters);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.GET), any(), eq(Object.class), eq(parameters));
    }

    @Test
    @DisplayName("POST без параметров и userId")
    void post_WithoutParametersAndUserId() {
        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.POST), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testPost("/test", requestBody);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.POST), any(), eq(Object.class));
    }

    @Test
    @DisplayName("POST с userId")
    void post_WithUserId() {
        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.POST), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testPost("/test", 1L, requestBody);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.POST), any(), eq(Object.class));
    }

    @Test
    @DisplayName("POST с userId и параметрами")
    void post_WithUserIdAndParameters() {
        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");
        Map<String, Object> parameters = Map.of("param1", "value1");

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.POST), any(), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testPost("/test", 1L, parameters, requestBody);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.POST), any(), eq(Object.class), eq(parameters));
    }

    @Test
    @DisplayName("PUT с userId")
    void put_WithUserId() {
        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.PUT), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testPut("/test", 1L, requestBody);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PUT), any(), eq(Object.class));
    }

    @Test
    @DisplayName("PUT с userId и параметрами")
    void put_WithUserIdAndParameters() {
        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");
        Map<String, Object> parameters = Map.of("param1", "value1");

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.PUT), any(), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testPut("/test", 1L, parameters, requestBody);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PUT), any(), eq(Object.class), eq(parameters));
    }

    @Test
    @DisplayName("PATCH без параметров и userId")
    void patch_WithoutParametersAndUserId() {
        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.PATCH), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testPatch("/test", requestBody);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PATCH), any(), eq(Object.class));
    }

    @Test
    @DisplayName("PATCH только с userId")
    void patch_WithUserIdOnly() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.PATCH), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testPatch("/test", 1L);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PATCH), any(), eq(Object.class));
    }

    @Test
    @DisplayName("PATCH с userId и body")
    void patch_WithUserIdAndBody() {
        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.PATCH), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testPatch("/test", 1L, requestBody);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PATCH), any(), eq(Object.class));
    }

    @Test
    @DisplayName("PATCH с userId, параметрами и body")
    void patch_WithUserIdParametersAndBody() {
        String requestBody = "test body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test response");
        Map<String, Object> parameters = Map.of("param1", "value1");

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.PATCH), any(), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testPatch("/test", 1L, parameters, requestBody);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.PATCH), any(), eq(Object.class), eq(parameters));
    }

    @Test
    @DisplayName("DELETE без параметров и userId")
    void delete_WithoutParametersAndUserId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.DELETE), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testDelete("/test");

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.DELETE), any(), eq(Object.class));
    }

    @Test
    @DisplayName("DELETE с userId")
    void delete_WithUserId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.DELETE), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testDelete("/test", 1L);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.DELETE), any(), eq(Object.class));
    }

    @Test
    @DisplayName("DELETE с userId и параметрами")
    void delete_WithUserIdAndParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Map<String, Object> parameters = Map.of("param1", "value1");

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.DELETE), any(), eq(Object.class), eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.testDelete("/test", 1L, parameters);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq("/test"), eq(HttpMethod.DELETE), any(), eq(Object.class), eq(parameters));
    }

    @Test
    @DisplayName("Заголовки устанавливаются корректно с userId")
    void defaultHeaders_WithUserId() {
        HttpHeaders headers = invokeDefaultHeaders(1L);

        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(List.of(MediaType.APPLICATION_JSON), headers.getAccept());
        assertEquals("1", headers.getFirst("X-Sharer-User-Id"));
    }

    @Test
    @DisplayName("Заголовки устанавливаются корректно без userId")
    void defaultHeaders_WithoutUserId() {
        HttpHeaders headers = invokeDefaultHeaders(null);

        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(List.of(MediaType.APPLICATION_JSON), headers.getAccept());
        assertNull(headers.getFirst("X-Sharer-User-Id"));
    }

    @Test
    @DisplayName("Обработка успешного ответа")
    void prepareGatewayResponse_Successful() {
        ResponseEntity<Object> originalResponse = ResponseEntity.ok("success");

        ResponseEntity<Object> preparedResponse = invokePrepareGatewayResponse(originalResponse);

        assertEquals(originalResponse, preparedResponse);
    }

    @Test
    @DisplayName("Обработка ошибки с телом ответа")
    void prepareGatewayResponse_ErrorWithBody() {
        ResponseEntity<Object> originalResponse = ResponseEntity.badRequest().body("error message");

        ResponseEntity<Object> preparedResponse = invokePrepareGatewayResponse(originalResponse);

        assertEquals(HttpStatus.BAD_REQUEST, preparedResponse.getStatusCode());
        assertEquals("error message", preparedResponse.getBody());
    }

    @Test
    @DisplayName("Обработка ошибки без тела ответа")
    void prepareGatewayResponse_ErrorWithoutBody() {
        ResponseEntity<Object> originalResponse = ResponseEntity.badRequest().build();

        ResponseEntity<Object> preparedResponse = invokePrepareGatewayResponse(originalResponse);

        assertEquals(HttpStatus.BAD_REQUEST, preparedResponse.getStatusCode());
        assertNull(preparedResponse.getBody());
    }

    @Test
    @DisplayName("Обработка исключения HttpClientErrorException")
    void makeAndSendRequest_HttpClientErrorException() {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad Request",
                HttpHeaders.EMPTY, "error".getBytes(), null);

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.GET), any(), eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> response = baseClient.testGet("/test");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Обработка исключения HttpServerErrorException")
    void makeAndSendRequest_HttpServerErrorException() {
        HttpServerErrorException exception = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "Server Error",
                HttpHeaders.EMPTY, "error".getBytes(), null);

        when(restTemplate.exchange(eq("/test"), eq(HttpMethod.GET), any(), eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> response = baseClient.testGet("/test");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    private HttpHeaders invokeDefaultHeaders(Long userId) {
        try {
            var method = BaseClient.class.getDeclaredMethod("defaultHeaders", Long.class);
            method.setAccessible(true);
            return (HttpHeaders) method.invoke(baseClient, userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity<Object> invokePrepareGatewayResponse(ResponseEntity<Object> response) {
        try {
            var method = BaseClient.class.getDeclaredMethod("prepareGatewayResponse", ResponseEntity.class);
            method.setAccessible(true);
            return (ResponseEntity<Object>) method.invoke(null, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}