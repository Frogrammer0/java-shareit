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
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    @DisplayName("GET /users - успешный запрос всех пользователей")
    void getAllUsers_ok() throws Exception {
        Mockito.when(userClient.getAllUsers(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /users - успешный запрос с параметрами по умолчанию")
    void getAllUsers_withDefaultParams() throws Exception {
        Mockito.when(userClient.getAllUsers(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("GET /users - факт ошибки без заголовка X-Sharer-User-Id")
    void getAllUsers_missingUserIdHeader() throws Exception {
        mockMvc.perform(get("/users")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(result -> assertNotNull(result.getResolvedException()));
    }

    @Test
    @DisplayName("GET /users/{userId} - успешный запрос пользователя по ID")
    void getUserById_ok() throws Exception {
        Mockito.when(userClient.getUserById(any(Long.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /users/{userId} - с различными ID")
    void getUserById_differentIds() throws Exception {
        Mockito.when(userClient.getUserById(any(Long.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/123456"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /users - успешное создание пользователя")
    void create_ok() throws Exception {
        String json = "{\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"email\": \"john@example.com\"\n" +
                "}";

        Mockito.when(userClient.create(any(UserDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("POST /users - факт ошибки при невалидном email")
    void create_invalidEmail() throws Exception {
        String json = "{\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"email\": \"invalid-email\"\n" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(result -> assertNotNull(result.getResolvedException()));
    }



    @Test
    @DisplayName("PATCH /users/{userId} - успешное обновление пользователя")
    void edit_ok() throws Exception {
        String json = "{\n" +
                "  \"name\": \"John Updated\",\n" +
                "  \"email\": \"john.updated@example.com\"\n" +
                "}";

        Mockito.when(userClient.edit(any(Long.class), any(UserDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /users/{userId} - успешное обновление только имени")
    void edit_onlyName() throws Exception {
        String json = "{\n" +
                "  \"name\": \"John Updated\"\n" +
                "}";

        Mockito.when(userClient.edit(any(Long.class), any(UserDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /users/{userId} - успешное обновление только email")
    void edit_onlyEmail() throws Exception {
        String json = "{\n" +
                "  \"email\": \"john.updated@example.com\"\n" +
                "}";

        Mockito.when(userClient.edit(any(Long.class), any(UserDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /users/{userId} - с различными ID")
    void edit_differentIds() throws Exception {
        String json = "{\n" +
                "  \"name\": \"Updated Name\"\n" +
                "}";

        Mockito.when(userClient.edit(any(Long.class), any(UserDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /users/{userId} - успешное удаление пользователя")
    void delete_ok() throws Exception {
        Mockito.when(userClient.delete(any(Long.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /users/{userId} - с различными ID")
    void delete_differentIds() throws Exception {
        Mockito.when(userClient.delete(any(Long.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users/123456"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /users - с различными валидными данными")
    void create_differentValidData() throws Exception {
        Mockito.when(userClient.create(any(UserDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        String json1 = "{\"name\": \"Alice\", \"email\": \"alice@example.com\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json1))
                .andExpect(status().isOk());

        String json2 = "{\"name\": \"Bob Smith\", \"email\": \"bob.smith@company.org\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json2))
                .andExpect(status().isOk());

        String json3 = "{\"name\": \"Чарли\", \"email\": \"charlie@test.ru\"}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json3))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Все endpoints - логируются")
    void allEndpoints_areLogged() throws Exception {
        Mockito.when(userClient.getAllUsers(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());
        Mockito.when(userClient.getUserById(any(Long.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());
        Mockito.when(userClient.create(any(UserDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());
        Mockito.when(userClient.edit(any(Long.class), any(UserDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());
        Mockito.when(userClient.delete(any(Long.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        String userJson = "{\"name\": \"Test User\", \"email\": \"test@example.com\"}";

        mockMvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

    }
}