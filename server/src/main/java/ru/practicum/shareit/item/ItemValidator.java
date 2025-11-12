package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ItemValidator {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemValidator(ItemRepository itemRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    public void validateUsed(long itemId, long userId) {
        LocalDateTime now = LocalDateTime.now();
        log.info("проверка возможности оставлять комментарий for userId = {}, itemId = {}", userId, itemId);
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, now)) {
            log.error("пользователь не пользовался данной вещью");
            throw new ValidationException("пользователь не пользовался данной вещью");
        }
    }

    public void hasAccess(long itemId, long userId) {
        if (!itemRepository.existsByIdAndOwnerId(itemId, userId)) {
            log.error("отсутствие прав доступа у пользователя");
            throw new ForbiddenException("отсутствие прав доступа на изменения ресурса");
        }
    }

    public void isItemExists(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            log.error("вещь с введенным id не найдена");
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена");
        }
    }


}
