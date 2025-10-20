package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;


@Data
@Builder
public class Booking {

    long id;
    LocalDate started;
    LocalDate ended;
    Item item;
    User booker;
    Status status;
}
