package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.InMemoryRequestStorage;
import ru.practicum.shareit.user.InMemoryUserStorage;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class ItemMapper {
    static InMemoryUserStorage userStorage;
    static InMemoryRequestStorage requestStorage;

    @Autowired
    public ItemMapper(InMemoryUserStorage userStorage, InMemoryRequestStorage requestStorage) {
      ItemMapper.userStorage = userStorage;
      ItemMapper.requestStorage = requestStorage;
    }

    public static ItemDto toItemDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .ownerId(item.getOwner().getId())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequestId() != null ? requestStorage.getRowRequestById(itemDto.getRequestId()) : null)
                .owner(userStorage.getRowUserById(itemDto.getOwnerId()))
                .build();
    }
}
