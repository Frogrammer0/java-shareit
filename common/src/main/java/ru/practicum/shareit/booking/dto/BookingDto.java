package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Status;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    long id;

    @FutureOrPresent
    LocalDateTime start;

    @Future
    LocalDateTime end;

    long itemId;
    long bookerId;
    Status status;
}
