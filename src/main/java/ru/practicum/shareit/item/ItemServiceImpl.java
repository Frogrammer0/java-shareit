package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.InMemoryRequestStorage;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@NoArgsConstructor(force = true)
public class ItemServiceImpl implements ItemService {
    ItemStorage itemStorage;
    ItemMapper itemMapper;
    ItemValidator itemValidator;
    UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(InMemoryItemStorage itemStorage, InMemoryUserStorage userStorage,
                           ItemMapper itemMapper, ItemValidator itemValidator,
                           InMemoryRequestStorage requestStorage) {
        this.itemStorage = itemStorage;
        this.itemMapper = itemMapper;
        this.itemValidator = itemValidator;
        this.userStorage = userStorage;
    }

    @Override
    public List<ItemDto> getAllItemsByUser(long userId) {
        log.info("вызван метод getAllItemsByUser в ItemService");
        userStorage.isUserExist(userId);
        return itemStorage.getAllItemsByUser(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long id) {
        log.info("вызван метод getItemById в ItemService");
        return itemMapper.toItemDto(getItemOrThrow(id));
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        log.info("вызван метод create в ItemService");
        itemValidator.validate(itemDto);
        User user = getUserOrThrow(userId);
        Item item = itemMapper.toItem(itemDto, user);
        return itemMapper.toItemDto(itemStorage.create(item));
    }

    @Override
    public void delete(long itemId, long userId) {
        log.info("вызван метод delete в ItemService");
        itemStorage.hasAccess(itemId, userId);
        itemStorage.isItemExist(itemId);
        userStorage.isUserExist(userId);
        itemStorage.delete(itemId, userId);
    }

    @Override
    public ItemDto edit(ItemDto itemDto, long userId, long itemId) {
        log.info("вызван метод edit в ItemService");
        itemStorage.isItemExist(itemId);
        itemStorage.hasAccess(itemId, userId);
        User user = getUserOrThrow(userId);
        Item item = itemMapper.toItem(itemDto, user);
        return itemMapper.toItemDto(itemStorage.edit(item, itemId));
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("вызван метод search в ItemService");
        String substr = text.toLowerCase().trim();

        if (text.isBlank()) {
            return List.of();
        }

        return itemStorage.search(substr).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item getItemOrThrow(long id) {
        log.info("вызван метод getItemOrThrow в ItemService");
        return itemStorage.getItemById(id).orElseThrow(
                () -> new NotFoundException("вещь с введенным id = " + id + " не найдена")
        );
    }

    private User getUserOrThrow(long userId) {
        log.info("вызван метод getUserOrThrow в ItemService");
        return userStorage.getUserById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь c id " + userId + " не найден")
        );
    }


}
