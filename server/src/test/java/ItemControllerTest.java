import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

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
        List<ItemDto> expectedItems = List.of(createTestItemDto());

        when(itemService.getAllItemsByUser(userId, 0, 10))
                .thenReturn(expectedItems);


        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(itemService).getAllItemsByUser(userId, 0, 10);
    }

    @Test
    void getAllItemsByUser_WhenMissingUserIdHeader_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllItemsByUser(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getItemById_WhenValidRequest_ShouldReturnItem() throws Exception {

        Long userId = 1L;
        Long itemId = 1L;
        ItemDto expectedItem = createTestItemDto();

        when(itemService.getItemById(itemId))
                .thenReturn(expectedItem);


        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        verify(itemService).getItemById(itemId);
    }

    @Test
    void create_WhenValidRequest_ShouldCreateItem() throws Exception {

        Long userId = 1L;
        ItemDto itemDto = createTestItemDto();
        ItemDto createdItem = createTestItemDto();

        when(itemService.create(any(ItemDto.class), eq(userId)))
                .thenReturn(createdItem);


        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"));

        verify(itemService).create(any(ItemDto.class), eq(userId));
    }

    @Test
    void create_WhenMissingUserIdHeader_ShouldReturnBadRequest() throws Exception {

        ItemDto itemDto = createTestItemDto();


        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(any(ItemDto.class), anyLong());
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
        ItemDto updatedItem = ItemDto.builder()
                .id(itemId)
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        when(itemService.edit(any(ItemDto.class), eq(userId), eq(itemId)))
                .thenReturn(updatedItem);


        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.available").value(false));

        verify(itemService).edit(any(ItemDto.class), eq(userId), eq(itemId));
    }

    @Test
    void edit_WhenPartialUpdate_ShouldWork() throws Exception {

        Long userId = 1L;
        Long itemId = 1L;
        ItemDto partialUpdateDto = ItemDto.builder()
                .name("Only Name Updated")
                .build();
        ItemDto updatedItem = ItemDto.builder()
                .id(itemId)
                .name("Only Name Updated")
                .description("Original Description")
                .available(true)
                .build();

        when(itemService.edit(any(ItemDto.class), eq(userId), eq(itemId)))
                .thenReturn(updatedItem);


        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Only Name Updated"));

        verify(itemService).edit(any(ItemDto.class), eq(userId), eq(itemId));
    }


    @Test
    void searchItem_WhenValidRequest_ShouldReturnSearchResults() throws Exception {

        Long userId = 1L;
        String searchText = "test";
        List<ItemDto> expectedItems = List.of(createTestItemDto());

        when(itemService.search(searchText, 0, 10))
                .thenReturn(expectedItems);


        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", searchText)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item"));

        verify(itemService).search(searchText, 0, 10);
    }

    @Test
    void searchItem_WhenEmptySearchText_ShouldReturnEmptyList() throws Exception {

        Long userId = 1L;
        String emptySearchText = "";

        when(itemService.search(emptySearchText, 0, 10))
                .thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", emptySearchText)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemService).search(emptySearchText, 0, 10);
    }

    @Test
    void postComment_WhenValidRequest_ShouldCreateComment() throws Exception {

        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .text("Test comment")
                .build();
        CommentDto createdComment = createTestCommentDto();

        when(itemService.postComment(itemId, userId, commentDto))
                .thenReturn(createdComment);


        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test comment"))
                .andExpect(jsonPath("$.authorName").value("Test Author"));

        verify(itemService).postComment(itemId, userId, commentDto);
    }

    @Test
    void postComment_WhenMissingUserIdHeader_ShouldReturnBadRequest() throws Exception {

        Long itemId = 1L;
        CommentDto commentDto = createTestCommentDto();


        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).postComment(anyLong(), anyLong(), any(CommentDto.class));
    }

    @Test
    void getAllItemsByUser_WithDifferentPagination_ShouldPassCorrectParameters() throws Exception {

        Long userId = 1L;
        List<ItemDto> expectedItems = List.of(createTestItemDto());

        when(itemService.getAllItemsByUser(userId, 5, 20))
                .thenReturn(expectedItems);


        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "5")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(itemService).getAllItemsByUser(userId, 5, 20);
    }

    @Test
    void searchItem_WithDifferentPagination_ShouldPassCorrectParameters() throws Exception {

        Long userId = 1L;
        String searchText = "test";

        when(itemService.search(searchText, 10, 5))
                .thenReturn(List.of());


        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", searchText)
                        .param("from", "10")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(itemService).search(searchText, 10, 5);
    }

    @Test
    void getItemById_WhenItemNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        Long userId = 1L;
        Long itemId = 999L;

        when(itemService.getItemById(itemId))
                .thenThrow(new NotFoundException("Item not found"));

        // Act & Assert
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

}