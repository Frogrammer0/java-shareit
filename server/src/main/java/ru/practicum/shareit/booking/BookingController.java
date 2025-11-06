package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
public class BookingController {

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId) {
        log.info("getBookingById in server/Booking Controller");
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingByUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", required = false) BookingState stateParam,
            @RequestParam(name = "from") Integer from,
            @RequestParam(name = "size") Integer size
    ) {
        log.info("getBookingByUser in server/Booking Controller");
        return bookingService.getBookingByUser(userId, stateParam, from, size);
    }

    @PostMapping
    public BookingResponseDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid BookingRequestDto bookingRequestDto) {
        log.info("create in server/Booking Controller");
        return bookingService.create(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approved(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam(name = "approved") boolean approved
    ) {
        log.info("approved in server/Booking Controller");
        return bookingService.approved(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingByUserItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false) BookingState state,
            @RequestParam(name = "from") Integer from,
            @RequestParam (name = "size") Integer size

    ) {
        log.info("getBookingByUserItem in server/Booking Controller");
        return bookingService.getBookingByUserItem(userId, state, from, size);
    }

}
