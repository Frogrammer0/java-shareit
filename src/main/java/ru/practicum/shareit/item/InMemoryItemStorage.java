package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemStorage {
    private final UserService userService;
    private final ItemValidator itemValidator;
    private final ItemMapper itemMapper;
    private final Map<Long, Item> items = new HashMap<>();


    @Autowired
    public InMemoryItemStorage(UserService userService, ItemValidator itemValidator, ItemMapper itemMapper) {
        this.userService = userService;
        this.itemValidator = itemValidator;
        this.itemMapper = itemMapper;
    }

    public Collection<Item> getAllItemsByUser(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    public Item getRowItem(long id) {
        return items.get(id);
    }

    public ItemDto getItemById(long id) {
        return itemMapper.toItemDto(items.get(id));
    }

    public ItemDto create(ItemDto itemDto, long userId) {
        log.info("вещь {} попала в метод create для добавления в базу", itemDto);

        itemValidator.validate(itemDto);

        userService.getUserById(userId);
        itemDto.setOwnerId(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setId(getNextId());

        items.put(item.getId(), item);
        log.info("вещь {} помещена в базу", item);
        return itemMapper.toItemDto(item);
    }

    public ItemDto edit(ItemDto newItemDto, long userId, long itemId) {
        log.info("вещь {} попала в метод edit для изменения в базе", newItemDto);
        newItemDto.setId(itemId);
        hasAccess(itemId, userId);
        isItemExist(itemId);
        notNullId(itemId);
        notNullId(userId);


        Item oldItem = items.get(newItemDto.getId());

        if (newItemDto.getName() != null) {
            oldItem.setName(newItemDto.getName());
        }

        if (newItemDto.getDescription() != null) {
            oldItem.setDescription(newItemDto.getDescription());
        }

        if (newItemDto.getAvailable() != null) {
            oldItem.setAvailable(newItemDto.getAvailable());
        }

        return itemMapper.toItemDto(oldItem);

    }

    public void delete(Long itemId, long userId) {
        hasAccess(itemId, userId);
        isItemExist(itemId);
        notNullId(itemId);
        notNullId(userId);


        items.remove(itemId);
        log.info("пользователь с id = {} удален", itemId);
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

    private void hasAccess(long itemId, long userId) {
        if (items.get(itemId).getOwner().getId() != userId) {
            log.error("отсутствие прав доступа у пользователя");
            throw new ForbiddenException("отсутствие прав доступа на изменения ресурса");
        }
    }

    private void isItemExist(long itemId) {
        if (!items.containsKey(itemId)) {
            log.error("вещь с введенным id не найдена");
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена");
        }
    }

    private void notNullId(long id) {
        if (id == 0) {
            log.error("введен id равный 0");
            throw new ValidationException("Id должен быть указан");
        }
    }

    public List<ItemDto> search(String text) {
        String substr = text.toLowerCase().trim();

        if (text.isBlank()) {
            return List.of();
        }

        return items.values().stream()
                .filter(item ->
                        (item.getName().toLowerCase().contains(substr) ||
                                item.getDescription().toLowerCase().contains(substr)) &&
                                (item.getAvailable())
                        )
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
