package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.util.List;


@AllArgsConstructor
@Builder
@Data
public class ItemDto {

    long id;
    UserShortDto owner;
    String name;
    String description;
    Boolean available;
    Long requestId;
    BookingShortDto lastBooking;
    BookingShortDto nextBooking;
    List<CommentDto> commentDto;

}
