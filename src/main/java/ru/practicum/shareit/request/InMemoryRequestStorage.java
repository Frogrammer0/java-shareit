package ru.practicum.shareit.request;


import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class InMemoryRequestStorage {
    private final Map<Long, ItemRequest> requests = new HashMap<>();

    public Collection<ItemRequest> getAllRequest() {
        return requests.values();
    }

    public ItemRequest getRequestById(long id) {
        return requests.get(id);
    }
}
