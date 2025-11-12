package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItemsByUser(long userId, Integer from, Integer size);

    ItemDto getItemById(long id);

    ItemDto create(ItemDto itemDto, long userId);

    void delete(long id, long userId);

    ItemDto edit(ItemDto itemDto, long userId, long itemId);

    List<ItemDto> search(String text, int from, int size);

    CommentDto postComment(long itemId, long userId, CommentDto commentDto);
}
