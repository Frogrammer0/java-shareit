package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    public Collection<Item> getAllItemsByUser(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }


    public Optional<Item> getItemById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    public Item create(Item item) {
        log.info("вещь {} попала в метод create для добавления в базу", item);
        item.setId(getNextId());

        items.put(item.getId(), item);
        log.info("вещь {} помещена в базу", item);
        return item;
    }

    public Item edit(Item newItem, long itemId) {
        log.info("вещь {} попала в метод edit для изменения в базе", newItem);
        newItem.setId(itemId);

        Item oldItem = items.get(itemId);

        if (newItem.getName() != null && !newItem.getName().isBlank()) {
            oldItem.setName(newItem.getName());
        }

        if (newItem.getDescription() != null && !newItem.getDescription().isBlank()) {
                oldItem.setDescription(newItem.getDescription());
        }

        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        return oldItem;

    }

    public void delete(Long itemId, long userId) {
        items.remove(itemId);
        log.info("пользователь с id = {} удален", itemId);
    }


    public List<Item> search(String text) {

        return items.values().stream()
                .filter(item ->
                        (item.getName().toLowerCase().contains(text) ||
                                item.getDescription().toLowerCase().contains(text)) &&
                                (item.getAvailable())
                )
                .collect(Collectors.toList());
    }

    public void hasAccess(long itemId, long userId) {
        if (items.get(itemId).getOwner().getId() != userId) {
            log.error("отсутствие прав доступа у пользователя");
            throw new ForbiddenException("отсутствие прав доступа на изменения ресурса");
        }
    }

    public void isItemExist(long itemId) {
        if (!items.containsKey(itemId)) {
            log.error("вещь с введенным id не найдена");
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена");
        }
    }


    private Long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        ++currentMaxId;
        log.info("создан id = {}", currentMaxId);
        return currentMaxId;
    }
}
