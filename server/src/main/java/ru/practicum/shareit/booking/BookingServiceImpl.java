package ru.practicum.shareit.booking;

import booking.BookingState;
import booking.Status;
import booking.dto.BookingRequestDto;
import booking.dto.BookingResponseDto;
import exceptions.NotFoundException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public BookingResponseDto getBookingById(long userId, long bookingId) {
        log.info("получение бронирования в BookingServiceImpl");
        userValidator.isUserExists(userId);
        Booking booking = getBookingOrThrow(bookingId);
        bookingValidator.isBookerOrOwner(userId, booking);
        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingByUser(long userId, BookingState state, Integer from, Integer size) {
        log.info("получение бронирований по пользователю");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findCurrentByBookerId(userId, now,
                    now, from, size);
            case PAST -> bookingRepository.findPastByBookerId(userId, now, from, size);
            case FUTURE -> bookingRepository.findFutureByBookerId(userId, now, from, size);
            ///
            case WAITING -> bookingRepository.findStatusByBookerId(userId, Status.WAITING, from, size);
            case REJECTED -> bookingRepository.findStatusByBookerId(userId, Status.REJECTED, from, size);
            default -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        };

        return bookings.stream()
                .map(bookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
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
    public List<BookingResponseDto> getBookingByUserItem(long userId, BookingState state, Integer from, Integer size) {
        log.info("получение бронирований по вещам пользователя");
        bookingValidator.hasItem(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findCurrentByOwnerItem(userId, now, now, from, size);
            case PAST -> bookingRepository.findPastByOwnerItem(userId, now, from, size);
            case FUTURE -> bookingRepository.findFutureByOwnerItem(userId, now, from, size);
            case WAITING -> bookingRepository.findStatusByOwnerItem(userId, Status.WAITING, from, size);
            case REJECTED -> bookingRepository.findStatusByOwnerItem(userId, Status.REJECTED, from, size);
            default -> bookingRepository.findAllByOwner(userId, from, size);
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
