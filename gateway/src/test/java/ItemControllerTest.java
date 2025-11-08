
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private ItemDto createTestItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
    }

    private CommentDto createTestCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("Test comment")
                .authorName("Test Author")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllItemsByUser_WhenValidRequest_ShouldReturnItems() throws Exception {
        Long userId = 1L;
        String expectedResponse = "{\"items\": []}";

        when(itemClient.getItemsByUser(eq(userId), anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }


    @Test
    void getAllItemsByUser_WhenMissingUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_WhenValidRequest_ShouldReturnItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        String expectedResponse = "{\"id\": 1, \"name\": \"Test Item\"}";

        when(itemClient.getItemById(itemId, userId))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void create_WhenValidRequest_ShouldCreateItem() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = createTestItemDto();
        String expectedResponse = "{\"id\": 1, \"name\": \"Test Item\"}";

        when(itemClient.create(eq(userId), any(ItemDto.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }


    @Test
    void create_WhenMissingUserIdHeader_ShouldReturnBadRequest() throws Exception {
        ItemDto itemDto = createTestItemDto();

        mockMvc.perform(post("/items")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void edit_WhenValidRequest_ShouldUpdateItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();
        String expectedResponse = "{\"id\": 1, \"name\": \"Updated Name\"}";

        when(itemClient.edit(eq(itemId), eq(userId), any(ItemDto.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void edit_WhenPartialUpdate_ShouldWork() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto partialUpdateDto = ItemDto.builder()
                .name("Only Name Updated")
                .build();
        String expectedResponse = "{\"id\": 1, \"name\": \"Only Name Updated\"}";

        when(itemClient.edit(eq(itemId), eq(userId), any(ItemDto.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void delete_WhenValidRequest_ShouldDeleteItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        String expectedResponse = "{\"message\": \"Item deleted\"}";

        when(itemClient.delete(itemId, userId))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void searchItem_WhenValidRequest_ShouldReturnSearchResults() throws Exception {
        Long userId = 1L;
        String searchText = "test";
        String expectedResponse = "{\"items\": []}";

        when(itemClient.search(eq(searchText), eq(userId), anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", searchText)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void searchItem_WhenEmptySearchText_ShouldReturnEmptyList() throws Exception {
        Long userId = 1L;
        String emptySearchText = "";

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", emptySearchText)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]")); // Пустой JSON массив
    }


    @Test
    void postComment_WhenValidRequest_ShouldCreateComment() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = createTestCommentDto();
        String expectedResponse = "{\"id\": 1, \"text\": \"Test comment\"}";

        when(itemClient.postComment(eq(itemId), eq(userId), any(CommentDto.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }


    @Test
    void postComment_WhenMissingUserIdHeader_ShouldReturnBadRequest() throws Exception {
        Long itemId = 1L;
        CommentDto commentDto = createTestCommentDto();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllItemsByUser_WithDefaultPagination_ShouldUseDefaults() throws Exception {
        Long userId = 1L;
        String expectedResponse = "{\"items\": []}";

        when(itemClient.getItemsByUser(eq(userId), eq(0), eq(10)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void searchItem_WithDefaultPagination_ShouldUseDefaults() throws Exception {
        Long userId = 1L;
        String searchText = "test";
        String expectedResponse = "{\"items\": []}";

        when(itemClient.search(eq(searchText), eq(userId), eq(0), eq(10)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }


}