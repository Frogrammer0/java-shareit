package ru.practicum.shareit.request;


import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.user.UserShortDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class ItemRequestDto {

    long id;

    @NotBlank
    String description;

    UserShortDto requestor;

    LocalDateTime created;

    List<ItemShortDto> items;
}
