package ru.practicum.shareit.request;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryRequestStorage {
    private final Map<Long, ItemRequest> requests = new HashMap<>();

    public Collection<ItemRequestDto> getAllRequest() {
        return requests.values().stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequestById(long id) {
        return ItemRequestMapper.toItemRequestDto(requests.get(id));
    }

    public ItemRequest getRowRequestById(long id) {
        return requests.get(id);
    }

    public ItemRequestDto create(ItemRequestDto itemRequestDto, long userId) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            log.error("не указано описание");
            throw new ValidationException("не указано описание");
        }

        itemRequestDto.setRequestorId(userId);
        itemRequestDto.setId(getNextId());
        itemRequestDto.setCreated(LocalDate.now());

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        requests.put(itemRequest.getId(), itemRequest);

        return itemRequestDto;
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
