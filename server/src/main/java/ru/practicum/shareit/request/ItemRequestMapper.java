package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemShortDto;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserShortDto;

import java.util.List;

@NoArgsConstructor
@Component
public class ItemRequestMapper {


    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemShortDto> items) {

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(new UserShortDto(itemRequest.getRequestor().getId(), itemRequest.getRequestor().getName()))
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(user)
                .created(itemRequestDto.getCreated())
                .build();
    }


}
