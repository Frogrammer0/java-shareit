package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam int from,
            @RequestParam int size) {
        return itemService.getAllItemsByUser(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId
    ) {
        return itemService.getItemById(itemId);
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto edit(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemDto itemDto
    ) {
        return itemService.edit(itemDto, userId, itemId);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        itemService.delete(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam int from,
            @RequestParam int size
    ) {
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody CommentDto commentDto
    ) {
        return itemService.postComment(itemId, userId, commentDto);
    }

}
