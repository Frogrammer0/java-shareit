package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
@Component
public class ItemValidator {
    private ItemRepository itemRepository;

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

    public void hasAccess(long itemId, long userId) {
        if (itemRepository.existsByIdAndUserId(itemId, userId)) {
            log.error("отсутствие прав доступа у пользователя");
            throw new ForbiddenException("отсутствие прав доступа на изменения ресурса");
        }
    }

    public void isItemExists(long itemId) {
        if (itemRepository.existsById(itemId)) {
            log.error("вещь с введенным id не найдена");
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена");
        }
    }


}
