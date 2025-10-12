package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
@Component
public class ItemValidator {

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


}
