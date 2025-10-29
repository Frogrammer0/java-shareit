package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItemsByUser(long userId);

    ItemDto getItemById(long id);

    ItemDto create(ItemDto itemDto, long userId);

    void delete(long id, long userId);

    ItemDto edit(ItemDto itemDto, long userId, long itemId);

    List<ItemDto> search(String text);

    CommentDto postComment(long itemId, long userId, CommentDto commentDto);
}
