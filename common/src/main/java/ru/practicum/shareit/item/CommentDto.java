package ru.practicum.shareit.item;

import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
public class CommentDto {

    long id;
    String authorName;
    ItemShortDto item;
    String text;

    @PastOrPresent
    LocalDateTime created;
}
