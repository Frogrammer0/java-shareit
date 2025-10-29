package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    //CURRENT
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId, LocalDateTime now1,
                                                                             LocalDateTime now2);

    //PAST
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime now);

    //FUTURE
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime now);

    //STATUS
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    //ALL OWNER
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    //CURRENT OWNER
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long ownerId, LocalDateTime now1,
                                                                                LocalDateTime now2);

    //PAST OWNER
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime now);

    //FUTURE OWNER
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime now);

    //STATUS OWNER
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    //LAST BOOKING
    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
            long itemId, LocalDateTime now, Status status
    );

    //NEXT BOOKING
    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
            long itemId, LocalDateTime now, Status status
    );

    //IS USER GETTING ITEM
    boolean existsByBookerIdAndItemIdAndEndBefore(
            long bookerId, long itemId, LocalDateTime now
    );

    //ALL BOOKINGS FOR ITEMS
    List<Booking> findAllByItemIdInAndStatusOrderByStartAsc(List<Long> itemId, Status status);


}
