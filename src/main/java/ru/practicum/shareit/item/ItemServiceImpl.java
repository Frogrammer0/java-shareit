package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ItemServiceImpl implements ItemService {
    InMemoryItemStorage itemStorage;

    @Override
    public List<ItemDto> getAllItems() {
        return itemStorage.getAllItems().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long id) {
        return itemStorage.getItemById(id);
    }

    @Override
    public ItemDto create(ItemDto itemDto) {
        return itemStorage.create(itemDto);
    }

    @Override
    public ItemDto update(ItemDto itemDto) {
        return itemStorage.update(itemDto);
    }

    @Override
    public void delete(long id){
      itemStorage.delete(id);
    }

}
