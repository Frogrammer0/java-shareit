import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidator;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemRequestMapper requestMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void create_ShouldCreateRequest() {
        long userId = 1L;
        User user = User.builder().id(userId).build();
        ItemRequestDto requestDto = ItemRequestDto.builder().description("Need item").build();
        ItemRequest savedRequest = ItemRequest.builder().id(1L).build();
        ItemRequestDto resultDto = ItemRequestDto.builder().id(1L).build();
        List<ItemShortDto> items = List.of();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestMapper.toItemRequest(any(), any())).thenReturn(savedRequest);
        when(requestRepository.save(savedRequest)).thenReturn(savedRequest);
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of());
        when(requestMapper.toItemRequestDto(savedRequest, items)).thenReturn(resultDto);

        ItemRequestDto result = itemRequestService.create(requestDto, userId);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(requestRepository).save(savedRequest);
    }

    @Test
    void create_WhenUserNotFound_ShouldThrowException() {
        long userId = 1L;
        ItemRequestDto requestDto = ItemRequestDto.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.create(requestDto, userId));
    }




    @Test
    void getRequestById_ShouldReturnRequest() {
        long requestId = 1L;
        ItemRequest request = ItemRequest.builder().id(requestId).build();
        ItemRequestDto requestDto = ItemRequestDto.builder().id(requestId).build();
        List<ItemShortDto> items = List.of();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(List.of());
        when(requestMapper.toItemRequestDto(request, items)).thenReturn(requestDto);

        ItemRequestDto result = itemRequestService.getRequestById(requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        verify(requestRepository).findById(requestId);
    }

    @Test
    void getRequestById_WhenNotFound_ShouldThrowException() {
        long requestId = 1L;
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(requestId));
    }

    @Test
    void getRequestsByUser_WithEmptyResult_ShouldReturnEmptyList() {
        long userId = 1L;
        doNothing().when(userValidator).isUserExists(userId);
        when(requestRepository.findAllByRequestorId(userId)).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getRequestsByUser(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRequests_WithEmptyResult_ShouldReturnEmptyList() {
        when(requestRepository.findAllRequests(0, 10)).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


}