package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
public class CommentDto {

    long id;
    UserShortDto author;
    ItemShortDto item;
    String text;
    LocalDateTime created;
}
