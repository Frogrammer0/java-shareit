package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */

@Data
@Builder
public class Booking {

    long id;
    LocalDate start;
    LocalDate end;
    Item item;
    User booker;
    Status status;
}
