package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */

@Data
@Builder
public class BookingDto {

    long id;
    LocalDate start;
    LocalDate end;
    long itemId;
    long bookerId;
    Status status;
}
