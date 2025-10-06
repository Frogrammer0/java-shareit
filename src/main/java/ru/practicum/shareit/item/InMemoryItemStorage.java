package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class InMemoryItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    public Collection<Item> getAllItems() {
        return items.values();
    }

    public Item getRowItem(long id) {
        return items.get(id);
    }

    public ItemDto getItemById(long id) {
        return ItemMapper.toItemDto(items.get(id));
    }

    public ItemDto create(ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);

        item.setId(getNextId());

        items.put(item.getId(), item);

        return ItemMapper.toItemDto(item);
    }

    public ItemDto update(ItemDto newItemDto) {
        if (newItemDto.getId() == 0) {
            log.error("введен id равный 0");
            throw new ValidationException("Id должен быть указан");
        }

        if (items.containsKey(newItemDto.getId())) {
            Item oldItem = items.get(newItemDto.getId());

            if (newItemDto.getName() != null) {
                oldItem.setName(newItemDto.getName());
            }

            if(newItemDto.getDescription() != null) {
                oldItem.setDescription(newItemDto.getDescription());
            }

            if (newItemDto.getIsAvailable() != null) {
                oldItem.setIsAvailable(newItemDto.getIsAvailable());
            }

            return ItemMapper.toItemDto(oldItem);
        }

        log.error("вещь с введенным id не найден");
        throw new NotFoundException("Вещь с id = " + newItemDto.getId() + " не найден");
    }

    public void delete(Long id) {
        if (id == 0) {
            log.error("введен id равный 0");
            throw new ValidationException("Id должен быть указан");
        }

        if (!items.containsKey(id)) {
            log.error("вещь с введенным id не найдена");
            throw new NotFoundException("Вещь с id = " + id + " не найдена");
        }

        items.remove(id);
        log.info("пользователь с id = {} удален", id);
    }

    private Long getNextId() {
        log.info("создан id");
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
