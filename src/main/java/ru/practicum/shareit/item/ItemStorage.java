package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

interface ItemStorage {

    Collection<Item> getAllItemsByUser(long userId);

    Optional<Item> getItemById(long id);

    Item create(Item item, User user);

    Item edit(Item newItem, long userId, long itemId);

    void delete(Long itemId, long userId);
}
