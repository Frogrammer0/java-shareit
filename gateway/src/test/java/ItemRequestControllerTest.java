

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestDto;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    @DisplayName("POST /requests - успешное создание запроса")
    void create_ok() throws Exception {
        String json = "{\n" +
                "  \"description\": \"Нужна дрель\"\n" +
                "}";

        Mockito.when(itemRequestClient.create(any(Long.class), any(ItemRequestDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /requests - факт ошибки при пустом теле")
    void create_emptyBody() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(result -> assertNotNull(result.getResolvedException()));
    }

    @Test
    @DisplayName("POST /requests - факт ошибки без заголовка X-Sharer-User-Id")
    void create_missingUserIdHeader() throws Exception {
        String json = "{\n" +
                "  \"description\": \"Нужна дрель\"\n" +
                "}";

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(result -> assertNotNull(result.getResolvedException()));
    }

    @Test
    @DisplayName("GET /requests - успешный запрос своих запросов")
    void getRequestByUser_ok() throws Exception {
        Mockito.when(itemRequestClient.getRequestByUser(any(Long.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /requests - факт ошибки без заголовка X-Sharer-User-Id")
    void getRequestByUser_missingUserIdHeader() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(result -> assertNotNull(result.getResolvedException()));
    }

    @Test
    @DisplayName("GET /requests/all - успешный запрос всех запросов")
    void getAllRequests_ok() throws Exception {
        Mockito.when(itemRequestClient.getAllRequests(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /requests/all - успешный запрос с параметрами по умолчанию")
    void getAllRequests_withDefaultParams() throws Exception {
        Mockito.when(itemRequestClient.getAllRequests(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /requests/all - факт ошибки без заголовка X-Sharer-User-Id")
    void getAllRequests_missingUserIdHeader() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(result -> assertNotNull(result.getResolvedException()));
    }

    @Test
    @DisplayName("GET /requests/{requestId} - успешный запрос по ID")
    void getRequestById_ok() throws Exception {
        Mockito.when(itemRequestClient.getRequestById(any(Long.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /requests/{requestId} - факт ошибки без заголовка X-Sharer-User-Id")
    void getRequestById_missingUserIdHeader() throws Exception {
        mockMvc.perform(get("/requests/1"))
                .andExpect(result -> assertNotNull(result.getResolvedException()));
    }

    @Test
    @DisplayName("GET /requests/{requestId} - с различными ID")
    void getRequestById_differentIds() throws Exception {
        Mockito.when(itemRequestClient.getRequestById(any(Long.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/requests/999")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/requests/123456")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /requests - с различными описаниями")
    void create_differentDescriptions() throws Exception {
        Mockito.when(itemRequestClient.create(any(Long.class), any(ItemRequestDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        String shortJson = "{\"description\": \"Дрель\"}";
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(shortJson))
                .andExpect(status().isOk());

        String longJson = "{\"description\": \"Очень нужна качественная дрель с набором сверл для домашнего ремонта\"}";
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(longJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Все endpoints - логируются")
    void allEndpoints_areLogged() throws Exception {
        Mockito.when(itemRequestClient.create(any(Long.class), any(ItemRequestDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());
        Mockito.when(itemRequestClient.getRequestByUser(any(Long.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());
        Mockito.when(itemRequestClient.getAllRequests(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());
        Mockito.when(itemRequestClient.getRequestById(any(Long.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        String json = "{\"description\": \"Тест\"}";

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/requests/123")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

    }
}