import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService requestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private ItemRequestDto itemRequestDto;
    private List<ItemRequestDto> itemRequestDtoList;

    @BeforeEach
    void setUp() {
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need item")
                .created(LocalDateTime.now())
                .build();

        itemRequestDtoList = List.of(itemRequestDto);
    }

    @Test
    void create_ShouldCallServiceAndReturnDto() {
        // given
        long userId = 1L;
        when(requestService.create(itemRequestDto, userId)).thenReturn(itemRequestDto);

        // when
        ItemRequestDto result = itemRequestController.create(userId, itemRequestDto);

        // then
        assertNotNull(result);
        assertEquals(itemRequestDto, result);
        verify(requestService).create(itemRequestDto, userId);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void getRequestsByUser_ShouldCallServiceAndReturnList() {
        // given
        long userId = 1L;
        when(requestService.getRequestsByUser(userId)).thenReturn(itemRequestDtoList);

        // when
        List<ItemRequestDto> result = itemRequestController.getRequestsByUser(userId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto, result.get(0));
        verify(requestService).getRequestsByUser(userId);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void getRequestsByUser_WhenEmptyList_ShouldReturnEmptyList() {
        // given
        long userId = 1L;
        when(requestService.getRequestsByUser(userId)).thenReturn(List.of());

        // when
        List<ItemRequestDto> result = itemRequestController.getRequestsByUser(userId);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(requestService).getRequestsByUser(userId);
    }

    @Test
    void getAllRequests_WithDefaultParameters_ShouldCallService() {
        // given
        Integer from = 0;
        Integer size = 10;
        when(requestService.getAllRequests(from, size)).thenReturn(itemRequestDtoList);

        // when
        List<ItemRequestDto> result = itemRequestController.getAllRequests(from, size);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(requestService).getAllRequests(from, size);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void getAllRequests_WithCustomParameters_ShouldCallService() {
        // given
        Integer from = 5;
        Integer size = 20;
        when(requestService.getAllRequests(from, size)).thenReturn(itemRequestDtoList);

        // when
        List<ItemRequestDto> result = itemRequestController.getAllRequests(from, size);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(requestService).getAllRequests(from, size);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void getAllRequests_WhenEmptyResult_ShouldReturnEmptyList() {
        // given
        Integer from = 0;
        Integer size = 10;
        when(requestService.getAllRequests(from, size)).thenReturn(List.of());

        // when
        List<ItemRequestDto> result = itemRequestController.getAllRequests(from, size);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(requestService).getAllRequests(from, size);
    }

    @Test
    void getRequestById_ShouldCallServiceAndReturnDto() {
        // given
        Long requestId = 1L;
        when(requestService.getRequestById(requestId)).thenReturn(itemRequestDto);

        // when
        ItemRequestDto result = itemRequestController.getRequestById(requestId);

        // then
        assertNotNull(result);
        assertEquals(itemRequestDto, result);
        verify(requestService).getRequestById(requestId);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void getRequestById_WithDifferentId_ShouldCallService() {
        // given
        Long requestId = 999L;
        ItemRequestDto differentDto = ItemRequestDto.builder()
                .id(999L)
                .description("Different request")
                .created(LocalDateTime.now())
                .build();

        when(requestService.getRequestById(requestId)).thenReturn(differentDto);

        // when
        ItemRequestDto result = itemRequestController.getRequestById(requestId);

        // then
        assertNotNull(result);
        assertEquals(differentDto, result);
        assertEquals(999L, result.getId());
        verify(requestService).getRequestById(requestId);
    }

    @Test
    void create_WithDifferentUser_ShouldCallService() {
        // given
        long userId = 999L;
        ItemRequestDto differentDto = ItemRequestDto.builder()
                .id(2L)
                .description("Another request")
                .created(LocalDateTime.now())
                .build();

        when(requestService.create(differentDto, userId)).thenReturn(differentDto);

        // when
        ItemRequestDto result = itemRequestController.create(userId, differentDto);

        // then
        assertNotNull(result);
        assertEquals(differentDto, result);
        verify(requestService).create(differentDto, userId);
    }

    @Test
    void create_WithFullItemRequestDto_ShouldCallService() {
        // given
        long userId = 1L;
        ItemRequestDto fullDto = ItemRequestDto.builder()
                .id(1L)
                .description("Full description")
                .created(LocalDateTime.now())
                .items(List.of()) // можно добавить реальные ItemShortDto если нужны
                .build();

        when(requestService.create(fullDto, userId)).thenReturn(fullDto);

        // when
        ItemRequestDto result = itemRequestController.create(userId, fullDto);

        // then
        assertNotNull(result);
        assertEquals(fullDto, result);
        verify(requestService).create(fullDto, userId);
    }
}