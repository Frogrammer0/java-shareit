package ru.practicum.shareit.request;


import ru.practicum.shareit.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryRequestStorage implements ItemRequestStorage {
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    ItemRequestMapper itemRequestMapper;
    UserStorage userStorage;

    public Collection<ItemRequest> getAllRequest() {
        return requests.values();
    }

    public ItemRequest getRequestById(long id) {
        return requests.get(id);
    }

    public ItemRequest create(ItemRequestDto itemRequestDto, long userId) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            log.error("не указано описание");
            throw new ValidationException("не указано описание");
        }

        itemRequestDto.getRequestor().setId(userId);
        itemRequestDto.setId(getNextId());
        itemRequestDto.setCreated(LocalDateTime.now());
        User user = userStorage.getUserById(userId).orElseThrow();
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, user);
        requests.put(itemRequest.getId(), itemRequest);

        return itemRequest;
    }

    private Long getNextId() {
        long currentMaxId = requests.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        ++currentMaxId;
        log.info("создан id = {}", currentMaxId);
        return currentMaxId;
    }
}
