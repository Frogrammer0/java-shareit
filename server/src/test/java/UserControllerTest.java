import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto createTestUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();
    }

    private UserDto createTestUserDto(Long id, String name, String email) {
        return UserDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    @Test
    void getAllUsers_WhenValidRequest_ShouldReturnUsers() throws Exception {
        List<UserDto> users = List.of(
                createTestUserDto(1L, "User 1", "user1@email.com"),
                createTestUserDto(2L, "User 2", "user2@email.com")
        );

        when(userService.getAllUsers(0, 10)).thenReturn(users);

        mockMvc.perform(get("/users")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[0].email").value("user1@email.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("User 2"))
                .andExpect(jsonPath("$[1].email").value("user2@email.com"));

        verify(userService).getAllUsers(0, 10);
    }

    @Test
    void getAllUsers_WithDifferentPagination_ShouldPassCorrectParameters() throws Exception {
        List<UserDto> users = List.of(createTestUserDto());

        when(userService.getAllUsers(5, 20)).thenReturn(users);

        mockMvc.perform(get("/users")
                        .param("from", "5")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(userService).getAllUsers(5, 20);
    }

    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() throws Exception {
        when(userService.getAllUsers(0, 10)).thenReturn(List.of());

        mockMvc.perform(get("/users")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService).getAllUsers(0, 10);
    }

    @Test
    void getAllUsers_WhenMissingParameters_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/users")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/users")
                        .param("from", "0"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).getAllUsers(anyInt(), anyInt());
    }


    @Test
    void getUserById_WhenValidRequest_ShouldReturnUser() throws Exception {
        Long userId = 1L;
        UserDto userDto = createTestUserDto();

        when(userService.getUserById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@email.com"));

        verify(userService).getUserById(userId);
    }


    @Test
    void create_WhenValidRequest_ShouldCreateUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("New User")
                .email("new@email.com")
                .build();

        UserDto createdUser = UserDto.builder()
                .id(1L)
                .name("New User")
                .email("new@email.com")
                .build();

        when(userService.create(any(UserDto.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("new@email.com"));

        verify(userService).create(any(UserDto.class));
    }

    @Test
    void edit_WhenValidRequest_ShouldUpdateUser() throws Exception {
        Long userId = 1L;
        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@email.com")
                .build();

        UserDto updatedUser = UserDto.builder()
                .id(userId)
                .name("Updated Name")
                .email("updated@email.com")
                .build();

        when(userService.edit(eq(userId), any(UserDto.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));

        verify(userService).edit(eq(userId), any(UserDto.class));
    }

    @Test
    void edit_WhenPartialUpdate_ShouldUpdateOnlyProvidedFields() throws Exception {
        Long userId = 1L;

        UserDto nameUpdateDto = UserDto.builder()
                .name("Only Name Updated")
                .build();

        UserDto nameUpdatedUser = UserDto.builder()
                .id(userId)
                .name("Only Name Updated")
                .email("original@email.com")
                .build();

        when(userService.edit(eq(userId), any(UserDto.class))).thenReturn(nameUpdatedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nameUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Only Name Updated"))
                .andExpect(jsonPath("$.email").value("original@email.com"));

        UserDto emailUpdateDto = UserDto.builder()
                .email("only.email@updated.com")
                .build();

        UserDto emailUpdatedUser = UserDto.builder()
                .id(userId)
                .name("Original Name")
                .email("only.email@updated.com")
                .build();

        when(userService.edit(eq(userId), any(UserDto.class))).thenReturn(emailUpdatedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Original Name"))
                .andExpect(jsonPath("$.email").value("only.email@updated.com"));

        verify(userService, times(2)).edit(eq(userId), any(UserDto.class));
    }



    @Test
    void delete_WhenValidRequest_ShouldDeleteUser() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).delete(userId);
    }




    @Test
    void create_WithNullRequestBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any(UserDto.class));
    }

    @Test
    void edit_WithNullRequestBody_ShouldReturnBadRequest() throws Exception {
        Long userId = 1L;

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).edit(anyLong(), any(UserDto.class));
    }

    @Test
    void getAllUsers_WithLargePagination_ShouldHandleCorrectly() throws Exception {
        List<UserDto> users = List.of(
                createTestUserDto(1L, "User 1", "user1@email.com"),
                createTestUserDto(2L, "User 2", "user2@email.com")
        );

        when(userService.getAllUsers(0, 100)).thenReturn(users);

        mockMvc.perform(get("/users")
                        .param("from", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(userService).getAllUsers(0, 100);
    }

    @Test
    void getUserById_WithInvalidPathVariable_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/users/{userId}", "invalid"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    void edit_WithInvalidPathVariable_ShouldReturnBadRequest() throws Exception {
        UserDto updateDto = createTestUserDto();

        mockMvc.perform(patch("/users/{userId}", "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).edit(anyLong(), any(UserDto.class));
    }

    @Test
    void delete_WithInvalidPathVariable_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/users/{userId}", "invalid"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).delete(anyLong());
    }
}