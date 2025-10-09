package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

interface ItemStorage {

    Collection<Item> getAllItemsByUser(long userId);

    Optional<Item> getItemById(long id);

    Item create(Item item);

    Item edit(Item newItem, long itemId);

    void delete(Long itemId, long userId);

    List<Item> search(String text);

    void hasAccess(long itemId, long userId);

    void isItemExist(long itemId);


}
