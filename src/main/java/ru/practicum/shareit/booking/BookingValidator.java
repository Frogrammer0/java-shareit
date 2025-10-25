package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;

import java.time.LocalDateTime;


@Slf4j
@Component
public class BookingValidator {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingValidator(ItemRepository itemRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    public void validateDate(BookingRequestDto bookingRequestDto) {

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

    public void isBookingExists(long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            log.error("бронирования с id = {} не существует", bookingId);
            throw new ValidationException("бронирования с id = " + bookingId + " не существует");
        }
    }


    public void isItemAvailable(long itemId) {
        if (!itemRepository.existsByIdAndAvailableIsTrue(itemId)) {
            log.error("вещь с id = {} недоступна", itemId);
            throw new ValidationException("вещь с id = " + itemId + "  недоступна");
        }
    }

    public void isBookerOrOwner(long userId, Booking booking) {
        if (!(booking.booker.getId() == userId || booking.item.getOwner().getId() == userId)) {
            log.error("отсутствие доступа у пользователя с id = {} ", userId);
            throw new ForbiddenException("отсутствие доступа у пользователя с id = " + userId);
        }
    }

    public void isOwner(long userId, Booking booking) {
        if (booking.item.getOwner().getId() != userId) {
            log.error("не является владельцем пользователь с id = {} ", userId);
            throw new ForbiddenException("отсутствие доступа у пользователя с id = " + userId);
        }
    }

    public void hasItem(long userId) {
        if (!itemRepository.existsByOwnerId(userId)) {
            log.error("отсутствие вещей у пользователя с id = {} ", userId);
            throw new NotFoundException("отсутствие вещей у пользователя с id = " + userId);
        }
    }

    public void statusIsWaiting(Booking booking) {
        if (booking.status != Status.WAITING) {
            log.error("неверный статус заявки: {}", booking.getStatus());
            throw new ValidationException("неверный статус заявки");
        }
    }
}
