import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingRequestDto requestDto;
    private BookingResponseDto responseDto;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        requestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        responseDto = BookingResponseDto.builder()
                .id(1L)
                .status(Status.WAITING)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .item(ItemShortDto.builder().id(1L).build())
                .booker(UserShortDto.builder().id(1L).build())
                .build();
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        Mockito.when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void getBookingByUser_shouldReturnList() throws Exception {
        Mockito.when(bookingService.getBookingByUser(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        Mockito.when(bookingService.create(any(BookingRequestDto.class), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void approveBooking_shouldReturnUpdatedBooking() throws Exception {
        responseDto.setStatus(Status.APPROVED);
        Mockito.when(bookingService.approved(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getBookingByUserItem_shouldReturnList() throws Exception {
        Mockito.when(bookingService.getBookingByUserItem(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }
}
