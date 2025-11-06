package ru.practicum.shareit.request;



import java.util.Collection;

public interface ItemRequestStorage {

    Collection<ItemRequest> getAllRequest();

    ItemRequest getRequestById(long id);

    ItemRequest create(ItemRequestDto itemRequestDto, long userId);
}
