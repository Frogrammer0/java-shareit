package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exceptions.ValidationException;

import java.time.LocalDateTime;

@Slf4j
@Component
public class BookingValidator {

    public void validateDate(BookingRequestDto bookingRequestDto) {
        log.info("валидация даты бронирования");
        if (bookingRequestDto.getStart() == null || bookingRequestDto.getEnd() == null) {
            throw new ValidationException("Дата начала и окончания бронирования не могут быть пустыми");
        }

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd()) ||
                bookingRequestDto.getStart().isEqual(bookingRequestDto.getEnd())) {
            throw new ValidationException("Дата начала бронирования должна быть раньше даты окончания");
        }

        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала бронирования не может быть в прошлом");
        }
    }
}
