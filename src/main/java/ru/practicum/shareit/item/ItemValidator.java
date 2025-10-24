package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

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

    public void validate(ItemDto itemDto) {
        validateName(itemDto.getName());
        validateDescription(itemDto.getDescription());
        validateAvailable(itemDto.getAvailable());
    }

    public void validateName(String name) {
        log.info("валидация названия вещи");

        if (name == null || name.isBlank()) {
            log.error("не указано название");
            throw new ValidationException("не указано название");
        }
    }

    public void validateAvailable(Boolean available) {
        log.info("валидация доступности вещи");
        if (available == null) {
            log.error("не указан статус вещи");
            throw new ValidationException("не указан статус вещи");
        }
    }

    public void validateDescription(String description) {
        log.info("валидация описания");
        if (description == null || description.isBlank()) {
            log.error("не указано описание");
            throw new ValidationException("не указано описание");
        }
    }

    public void validateUsed(long itemId, long userId) {
        LocalDateTime now = LocalDateTime.now();
        log.info("проверка возможности оставлять комментарий");
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
