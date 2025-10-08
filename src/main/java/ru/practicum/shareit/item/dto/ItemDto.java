package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@AllArgsConstructor
@Builder
@Data
public class ItemDto {

    long id;
    long ownerId;
    String name;
    String description;
    Boolean available;
    Long requestId;

}
