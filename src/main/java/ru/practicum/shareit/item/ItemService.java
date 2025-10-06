package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems();

    ItemDto getItemById(long id);

    ItemDto create(ItemDto itemDto);

    ItemDto update(ItemDto itemDto);

    void delete(long id);
}
