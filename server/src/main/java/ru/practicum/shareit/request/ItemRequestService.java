package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getRequestsByUser(long userId);

    List<ItemRequestDto> getAllRequests(int from, int size);

    ItemRequestDto getRequestById(long requestId);
}
