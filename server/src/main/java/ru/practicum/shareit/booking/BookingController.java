package ru.practicum.shareit.booking;

import booking.BookingState;
import booking.dto.BookingRequestDto;
import booking.dto.BookingResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class BookingController {

    BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingByUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", required = false) BookingState stateParam,
            @RequestParam(name = "from") Integer from,
            @RequestParam (name = "size") Integer size
    ) {
        return bookingService.getBookingByUser(userId, stateParam, from, size);
    }

    @PostMapping
    public BookingResponseDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.create(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approved(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved
    ) {
        return bookingService.approved(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingByUserItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false) BookingState state,
            @RequestParam(name = "from") Integer from,
            @RequestParam (name = "size") Integer size

    ) {
        return bookingService.getBookingByUserItem(userId, state, from, size);
    }

}
