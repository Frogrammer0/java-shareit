package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.InMemoryRequestStorage;
import ru.practicum.shareit.user.InMemoryUserStorage;


@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final InMemoryUserStorage userStorage;
    private final InMemoryRequestStorage requestStorage;


    public ItemDto toItemDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .ownerId(item.getOwner().getId())
                .build();
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequestId() != null ? requestStorage.getRowRequestById(itemDto.getRequestId()) : null)
                .owner(userStorage.getUserById(itemDto.getOwnerId()).orElseThrow(
                        () -> new NotFoundException("Пользователь не найден")
                ))
                .build();
    }
}
