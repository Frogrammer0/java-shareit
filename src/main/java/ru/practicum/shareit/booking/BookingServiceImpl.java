package ru.practicum.shareit.booking;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@NoArgsConstructor(force = true)
public class BookingServiceImpl implements BookingService {
    BookingValidator bookingValidator;
    UserRepository userRepository;
    ItemRepository itemRepository;
    BookingMapper bookingMapper;
    BookingRepository bookingRepository;
    UserValidator userValidator;

    @Autowired
    public BookingServiceImpl(BookingValidator bookingValidator, UserRepository userRepository,
                              BookingMapper bookingMapper, ItemRepository itemRepository,
                              BookingRepository bookingRepository, UserValidator userValidator) {
        this.bookingValidator = bookingValidator;
        this.userRepository = userRepository;
        this.bookingMapper = bookingMapper;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.userValidator = userValidator;
    }

    @Override
    public BookingResponseDto create(BookingRequestDto bookingRequestDto, long userId) {
        log.info("создание бронирования в BookingServiceImpl");
        User user = getUserOrThrow(userId);
        Item item = getItemOrThrow(bookingRequestDto.getItemId());
        bookingValidator.validateDate(bookingRequestDto);
        bookingValidator.isItemAvailable(bookingRequestDto.getItemId());
        Booking booking = bookingMapper.toBooking(bookingRequestDto, item, user);
        booking.setStatus(Status.WAITING);
        return bookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto approved(long ownerId, long bookingId, boolean approved) {
        log.info("подтверждение бронирования в BookingServiceImpl");
        Booking booking = getBookingOrThrow(bookingId);
        bookingValidator.isOwner(ownerId, booking);
        bookingValidator.statusIsWaiting(booking);
        userValidator.isUserExists(ownerId);
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(long userId, long bookingId) {
        log.info("получение бронирования в BookingServiceImpl");
        userValidator.isUserExists(userId);
        Booking booking = getBookingOrThrow(bookingId);
        bookingValidator.isBookerOrOwner(userId, booking);
        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingByUser(long userId, State state) {
        log.info("получение бронирований по пользователю");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now,
                    now);
            case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            default -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        };

        return bookings.stream()
                .map(bookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingByUserItem(long userId, State state) {
        log.info("получение бронирований по вещам пользователя");
        bookingValidator.hasItem(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now,
                    now);
            case PAST -> bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            default -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        };

        return bookings.stream().map(bookingMapper::toBookingResponseDto).collect(Collectors.toList());
    }

    private User getUserOrThrow(long userId) {
        log.info("вызван метод getUserOrThrow в BookingService для id = {}", userId);
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь c id " + userId + " не найден")
        );
    }

    private Item getItemOrThrow(long id) {
        log.info("вызван метод getItemOrThrow в BookingService для id = {}", id);
        return itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Вещь с введенным id = " + id + " не найдена")
        );
    }

    private Booking getBookingOrThrow(long id) {
        log.info("вызван метод getBookingOrThrow в BookingService");
        return bookingRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Бронирование с введенным id = " + id + " не найдено")
        );
    }
}
