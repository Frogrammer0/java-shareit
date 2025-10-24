package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.InMemoryRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@NoArgsConstructor(force = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemMapper itemMapper;
    BookingMapper bookingMapper;
    CommentMapper commentMapper;
    ItemValidator itemValidator;
    UserValidator userValidator;


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           ItemMapper itemMapper, ItemValidator itemValidator, CommentRepository commentRepository,
                           UserValidator userValidator, BookingRepository bookingRepository,
                           CommentMapper commentMapper, InMemoryRequestStorage requestStorage,
                           BookingMapper bookingMapper) {
        this.itemMapper = itemMapper;
        this.itemValidator = itemValidator;
        this.userValidator = userValidator;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;

    }


    @Override
    public List<ItemDto> getAllItemsByUser(long userId) {
        log.info("вызван метод getAllItemsByUser в ItemService");
        userValidator.isUserExists(userId);
        LocalDateTime now = LocalDateTime.now();
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> itemMapper.toItemDto(item, getCommentByItem(item.getId())))
                .map(item -> setLastAndNextBooking(item, now))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long id) {
        log.info("вызван метод getItemById в ItemService");
        return itemMapper.toItemDto(getItemOrThrow(id), getCommentByItem(id));
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        log.info("вызван метод create в ItemService");
        itemValidator.validate(itemDto);
        User user = getUserOrThrow(userId);
        Item item = itemMapper.toItem(itemDto, user);
        return itemMapper.toItemDto(itemRepository.save(item), getCommentByItem(item.getId()));
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
        return itemMapper.toItemDto(itemRepository.save(item), getCommentByItem(itemId));
    }

    @Override
    public List<ItemDto> search(String query) {
        log.info("вызван метод search в ItemService");

        if (query.isBlank()) {
            return List.of();
        }

        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue
                        (query, query)
                .stream()
                .filter(Item::getAvailable)
                .map(item -> itemMapper.toItemDto(item, getCommentByItem(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto postComment(long itemId, long userId, CommentDto commentDto) {
        log.info("вызван метод postComment в ItemService");
        commentDto.setCreated(LocalDateTime.now());
        itemValidator.validateUsed(itemId, userId);
        User author = getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);
        Comment comment = commentMapper.toComment(commentDto, item, author);
        return commentMapper.toCommentDto(commentRepository.save(comment));
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

    private ItemDto setLastAndNextBooking(ItemDto itemDto, LocalDateTime now) {
        log.info("вызван метод setLastAndNextBooking в ItemService");
        itemDto.setLastBooking(
                bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
                                itemDto.getId(), now, Status.APPROVED
                        )
                        .map(bookingMapper::toBookingShortDto)
                        .orElse(null)
        );
        itemDto.setNextBooking(
                bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                                itemDto.getId(), now, Status.APPROVED
                        )
                        .map(bookingMapper::toBookingShortDto)
                        .orElse(null)
        );

        return itemDto;
    }

    private List<CommentDto> getCommentByItem(long itemId) {
        log.info("вызван метод getCommentByItem в ItemService для вещи с id = {}", itemId);
        return commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }


}
