package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.InMemoryRequestStorage;
import ru.practicum.shareit.user.InMemoryUserStorage;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    static InMemoryUserStorage userStorage;
    static InMemoryRequestStorage requestStorage;

    public ItemMapper(InMemoryUserStorage userStorage, InMemoryRequestStorage requestStorage) {
      ItemMapper.userStorage = userStorage;
      ItemMapper.requestStorage = requestStorage;
    }

    public static ItemDto toItemDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.getIsAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .ownerId(item.getOwner().getId())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getIsAvailable())
                .request(requestStorage.getRequestById(itemDto.getRequestId()))
                .owner(userStorage.getRowUser(itemDto.getOwnerId()))
                .build();
    }
}
