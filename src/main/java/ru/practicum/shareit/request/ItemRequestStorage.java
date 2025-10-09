package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestStorage {

    Collection<ItemRequest> getAllRequest();

    ItemRequest getRequestById(long id);

    ItemRequest create(ItemRequestDto itemRequestDto, long userId);
}
