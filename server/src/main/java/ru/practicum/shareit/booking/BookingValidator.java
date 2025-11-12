package ru.practicum.shareit.booking;


import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemRepository;




@Slf4j
@Component
public class BookingValidator {

    private final ItemRepository itemRepository;

    @Autowired
    public BookingValidator(ItemRepository itemRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
    }

        public void isItemAvailable(long itemId) {
        log.info("валидация доступности вещи");
        if (!itemRepository.existsByIdAndAvailableIsTrue(itemId)) {
            log.error("вещь с id = {} недоступна", itemId);
            throw new ValidationException("вещь с id = " + itemId + "  недоступна");
        }
    }

    public void isOwner(long userId, Booking booking) {
        log.info("проверка прав доступа пользователя");
        if (booking.item.getOwner().getId() != userId) {
            log.error("не является владельцем пользователь с id = {} ", userId);
            throw new ForbiddenException("отсутствие доступа у пользователя с id = " + userId);
        }
    }

    public void hasItem(long userId) {
        log.info("проверка наличия вещей у пользователя");
        if (!itemRepository.existsByOwnerId(userId)) {
            log.error("отсутствие вещей у пользователя с id = {} ", userId);
            throw new NotFoundException("отсутствие вещей у пользователя с id = " + userId);
        }
    }

    public void isBookerOrOwner(long userId, Booking booking) {
        log.info("проверка прав доступа пользователя");
        if (!(booking.booker.getId() == userId || booking.item.getOwner().getId() == userId)) {
            log.error("отсутствие доступа у пользователя с id = {} ", userId);
            throw new ForbiddenException("отсутствие доступа у пользователя с id = " + userId);
        }
    }

    public void statusIsWaiting(Booking booking) {
        if (booking.getStatus() != Status.WAITING) {
            log.error("неверный статус заявки: {}", booking.getStatus());
            throw new ValidationException("неверный статус заявки");
        }
    }
}
