package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id) {
        return itemService.getItemById(id);
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto) {
        return itemService.create(itemDto);
    }

    @PutMapping
    public ItemDto update(@RequestBody ItemDto itemDto) {
        return itemService.update(itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        itemService.delete(id);
    }
}
