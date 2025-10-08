package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@NoArgsConstructor
public class ItemServiceImpl implements ItemService {
    InMemoryItemStorage itemStorage;
    ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(InMemoryItemStorage itemStorage, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.itemMapper = itemMapper;
    }

    @Override
    public List<ItemDto> getAllItemsByUser(long userId) {
        return itemStorage.getAllItemsByUser(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long id) {
        return itemStorage.getItemById(id);
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        log.info("вызван метод create в ItemService");
        return itemStorage.create(itemDto, userId);
    }

    @Override
    public void delete(long id, long userId) {
      itemStorage.delete(id, userId);
    }

    @Override
    public ItemDto edit(ItemDto itemDto, long userId, long itemId) {
        log.info("вызван метод edit в ItemService");
        return itemStorage.edit(itemDto, userId, itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("вызван метод search в ItemService");
        return itemStorage.search(text);
    }

}
