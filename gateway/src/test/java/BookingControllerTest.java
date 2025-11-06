import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItTestApplication.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Test
    @DisplayName("GET /bookings/{id} — успешный запрос")
    void getBooking_ok() throws Exception {
        Mockito.when(bookingClient.getBooking(any(Long.class), any(Long.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /bookings — успешный запрос")
    void getBookingsByUser_ok() throws Exception {
        Mockito.when(bookingClient.getBookings(any(Long.class), any(), any(Integer.class), any(Integer.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("GET /bookings — факт ошибки при неизвестном state")
    void getBookingsByUser_unknownState() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "WRONG_STATE")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(result -> assertNotNull(result.getResolvedException()));
    }


    @Test
    @DisplayName("POST /bookings — успешное создание брони")
    void bookItem_ok() throws Exception {

        String json = "{\n" +
                "  \"itemId\": 1,\n" +
                "  \"start\": \"2025-12-01T10:00:00\",\n" +
                "  \"end\": \"2025-12-05T10:00:00\"\n" +
                "}";

        Mockito.when(bookingClient.bookItem(any(Long.class), any(BookingRequestDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /bookings — факт ошибки при пустом теле")
    void bookItem_emptyBody() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(result -> assertNotNull(result.getResolvedException()));
    }

    @Test
    @DisplayName("PATCH /bookings/{id} — успешное одобрение брони")
    void approvedBooking_ok() throws Exception {
        Mockito.when(bookingClient.approvedBooking(eq(1L), eq(1L), eq(true)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /bookings/owner — успешный запрос")
    void getBookingByUserItem_ok() throws Exception {
        Mockito.when(bookingClient.getBookingsByUserItem(any(Long.class), any(), any(Integer.class), any(Integer.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "all")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

}
