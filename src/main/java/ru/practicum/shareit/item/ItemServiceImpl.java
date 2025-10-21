package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.InMemoryRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidator;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@NoArgsConstructor(force = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemMapper itemMapper;
    ItemValidator itemValidator;
    UserValidator userValidator;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           ItemMapper itemMapper, ItemValidator itemValidator,
                           UserValidator userValidator,
                           InMemoryRequestStorage requestStorage) {
        this.itemMapper = itemMapper;
        this.itemValidator = itemValidator;
        this.userValidator = userValidator;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;

    }


    @Override
    public List<ItemDto> getAllItemsByUser(long userId) {
        log.info("вызван метод getAllItemsByUser в ItemService");
        userValidator.isUserExists(userId);
        return itemRepository.findAllByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long id) {
        log.info("вызван метод getItemById в ItemService");
        return itemMapper.toItemDto(getItemOrThrow(id));
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        log.info("вызван метод create в ItemService");
        itemValidator.validate(itemDto);
        User user = getUserOrThrow(userId);
        Item item = itemMapper.toItem(itemDto, user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public void delete(long itemId, long userId) {
        log.info("вызван метод delete в ItemService");
        itemValidator.hasAccess(itemId, userId);
        itemValidator.isItemExists(itemId);
        userValidator.isUserExists(userId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto edit(ItemDto itemDto, long userId, long itemId) {
        log.info("вызван метод edit в ItemService");
        itemValidator.isItemExists(itemId);
        itemValidator.hasAccess(itemId, userId);
        User user = getUserOrThrow(userId);
        Item item = itemMapper.toItem(itemDto, user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> search(String query) {
        log.info("вызван метод search в ItemService");

        if (query.isBlank()) {
            return List.of();
        }

        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item getItemOrThrow(long id) {
        log.info("вызван метод getItemOrThrow в ItemService");
        return itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("вещь с введенным id = " + id + " не найдена")
        );
    }

    private User getUserOrThrow(long userId) {
        log.info("вызван метод getUserOrThrow в ItemService");
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь c id " + userId + " не найден")
        );
    }


}
