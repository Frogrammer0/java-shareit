
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingValidator bookingValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User createTestUser(Long id) {
        return User.builder()
                .id(id)
                .name("Test User")
                .email("test@email.com")
                .build();
    }

    private Item createTestItem(Long id, User owner) {
        return Item.builder()
                .id(id)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build();
    }

    private Booking createTestBooking(Long id, Item item, User booker) {
        return Booking.builder()
                .id(id)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void getBookingById_WhenValidData_ShouldReturnBooking() {
        Long userId = 1L;
        Long bookingId = 1L;
        User user = createTestUser(userId);
        User owner = createTestUser(2L);
        Item item = createTestItem(1L, owner);
        Booking booking = createTestBooking(bookingId, item, user);
        BookingResponseDto expectedDto = new BookingResponseDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        doNothing().when(bookingValidator).isBookerOrOwner(userId, booking);
        when(bookingMapper.toBookingResponseDto(booking)).thenReturn(expectedDto);

        BookingResponseDto result = bookingService.getBookingById(userId, bookingId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(userValidator).isUserExists(userId);
        verify(bookingRepository).findById(bookingId);
        verify(bookingValidator).isBookerOrOwner(userId, booking);
        verify(bookingMapper).toBookingResponseDto(booking);
    }

    @Test
    void getBookingById_WhenBookingNotFound_ShouldThrowException() {
        Long userId = 1L;
        Long bookingId = 999L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(userId, bookingId));
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void create_WhenValidData_ShouldCreateBooking() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = createTestUser(userId);
        User owner = createTestUser(2L);
        Item item = createTestItem(itemId, owner);

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Booking booking = createTestBooking(1L, item, user);
        BookingResponseDto expectedDto = new BookingResponseDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(bookingValidator).isItemAvailable(itemId);
        when(bookingMapper.toBooking(requestDto, item, user)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingResponseDto(booking)).thenReturn(expectedDto);

        BookingResponseDto result = bookingService.create(requestDto, userId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        assertEquals(Status.WAITING, booking.getStatus());
        verify(bookingRepository).save(booking);
        verify(bookingValidator).isItemAvailable(itemId);
    }

    @Test
    void create_WhenUserNotFound_ShouldThrowException() {
        Long userId = 999L;
        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(1L)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(requestDto, userId));
        verify(userRepository).findById(userId);
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void approved_WhenApprovedTrue_ShouldSetStatusApproved() {
        Long ownerId = 1L;
        Long bookingId = 1L;
        User booker = createTestUser(2L);
        User owner = createTestUser(ownerId);
        Item item = createTestItem(1L, owner);
        Booking booking = createTestBooking(bookingId, item, booker);
        BookingResponseDto expectedDto = new BookingResponseDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        doNothing().when(bookingValidator).isOwner(ownerId, booking);
        doNothing().when(bookingValidator).statusIsWaiting(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingResponseDto(booking)).thenReturn(expectedDto);

        BookingResponseDto result = bookingService.approved(ownerId, bookingId, true);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        assertEquals(Status.APPROVED, booking.getStatus());
        verify(bookingValidator).isOwner(ownerId, booking);
        verify(bookingValidator).statusIsWaiting(booking);
        verify(bookingRepository).save(booking);
    }

    @Test
    void approved_WhenApprovedFalse_ShouldSetStatusRejected() {
        Long ownerId = 1L;
        Long bookingId = 1L;
        User booker = createTestUser(2L);
        User owner = createTestUser(ownerId);
        Item item = createTestItem(1L, owner);
        Booking booking = createTestBooking(bookingId, item, booker);
        BookingResponseDto expectedDto = new BookingResponseDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        doNothing().when(bookingValidator).isOwner(ownerId, booking);
        doNothing().when(bookingValidator).statusIsWaiting(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingResponseDto(booking)).thenReturn(expectedDto);

        BookingResponseDto result = bookingService.approved(ownerId, bookingId, false);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        assertEquals(Status.REJECTED, booking.getStatus());
    }

    @Test
    void getBookingByUser_WithDifferentStates_ShouldReturnCorrectBookings() {

        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = List.of(
                createTestBooking(1L, createTestItem(1L, createTestUser(2L)), createTestUser(userId))
        );
        List<BookingResponseDto> expectedDtos = List.of(new BookingResponseDto());


        when(bookingRepository.findAllByBookerIdOrderByStartDesc(userId)).thenReturn(bookings);
        when(bookingMapper.toBookingResponseDto(any(Booking.class))).thenReturn(new BookingResponseDto());


        List<BookingResponseDto> result = bookingService.getBookingByUser(userId, BookingState.ALL, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(userId);
    }

}