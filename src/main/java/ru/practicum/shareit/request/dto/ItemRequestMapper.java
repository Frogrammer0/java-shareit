package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.InMemoryUserStorage;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class ItemRequestMapper {
    static InMemoryUserStorage userStorage;

    @Autowired
    public ItemRequestMapper(InMemoryUserStorage userStorage) {
        ItemRequestMapper.userStorage = userStorage;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(userStorage.getRowUserById(itemRequestDto.getRequestorId()))
                .created(itemRequestDto.getCreated())
                .build();
    }


}
