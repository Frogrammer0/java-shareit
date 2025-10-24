package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ItemMapper {


    public ItemDto toItemDto(Item item, List<CommentDto> commentsDto) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequests() != null ? item.getRequests().getId() : null)
                .owner(new UserShortDto(item.getOwner().getId(), item.getOwner().getName()))
                .comments(commentsDto)
                .build();
    }

    public Item toItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
    }

    public ItemShortDto toItemShortDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}
