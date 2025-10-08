package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.InMemoryUserStorage;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class ItemRequestMapper {
    static InMemoryUserStorage userStorage;

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(userStorage.getRowUserById(itemRequestDto.getRequestorId()))
                .created(itemRequestDto.getCreated())
                .build();
    }


}
