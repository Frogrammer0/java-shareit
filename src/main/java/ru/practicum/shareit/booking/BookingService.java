package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(BookingRequestDto bookingRequestDto, long userId);

    BookingResponseDto approved(long bookingId, long ownerId, boolean approved);

    BookingResponseDto getBookingById(long userId, long bookingId);

    List<BookingResponseDto> getBookingByUser(long userId, State state);

    List<BookingResponseDto> getBookingByUserItem(long userId, State state);
}
