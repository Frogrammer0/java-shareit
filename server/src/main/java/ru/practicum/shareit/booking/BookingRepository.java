package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    //CURRENT
    @Query(value = """
            select * from bookings b
            where b.booker_id = :bookerId
            and b.started < :now1 and b.ended > :now2
            order by b.started desc
            limit :size offset :from
            """, nativeQuery = true)
    List<Booking> findCurrentByBookerId(@Param("bookerId") long bookerId,
                                        @Param("now1") LocalDateTime now1,
                                        @Param("now2") LocalDateTime now2,
                                        @Param("from") int from,
                                        @Param("size") int size);

    //PAST
    @Query(value = """
            select * from bookings b
            where b.booker_id = :bookerId
            and b.ended < :now
            order by b.started desc
            limit :size offset :from
            """, nativeQuery = true)
    List<Booking> findPastByBookerId(@Param("bookerId") long bookerId,
                                     @Param("now") LocalDateTime now,
                                     @Param("from") int from,
                                     @Param("size") int size);

    //FUTURE
    @Query(value = """
            select * from bookings b
            where b.booker_id = :bookerId
            and b.started > :now
            order by b.started desc
            limit :size offset :from
            """, nativeQuery = true)
    List<Booking> findFutureByBookerId(@Param("bookerId") long bookerId,
                                       @Param("now") LocalDateTime now,
                                       @Param("from") int from,
                                       @Param("size") int size);

    //STATUS
    @Query(value = """
            select * from booking b
            where b.booker_id = :bookerId
            and b.status = :status
            order by b.started desc
            limit :size offset :from
            """, nativeQuery = true)
    List<Booking> findStatusByBookerId(@Param("bookerId") long bookerId,
                                       @Param("status") Status status,
                                       @Param("size") int size,
                                       @Param("from") int from);

    //ALL BY OWNER
    @Query(value = """
            select * from booking b
            join items i on b.item_id = i.id
            where i.user_id = :ownerId
            order by b.started desc
            limit :size offset :from
            """, nativeQuery = true)
    List<Booking> findAllByOwner(@Param("ownerId") long ownerId,
                                 @Param("from") int from,
                                 @Param("size") int size);

    //CURRENT BY OWNER
    @Query(value = """
            select * from booking b
            join items i on b.item_id = i.id
            where i.user_id = :ownerId
            and b.started < :now1
            and b.ended > :now2
            order by b.started desc
            limit :size offset :from
            """, nativeQuery = true)
    List<Booking> findCurrentByOwnerItem(@Param("ownerId") long ownerId,
                                         @Param("now1") LocalDateTime now1,
                                         @Param("now2") LocalDateTime now2,
                                         @Param("from") int from,
                                         @Param("size") int size);

    //PAST BY OWNER
    @Query(value = """
            select * from booking b
            join items i on b.item_id = i.id
            where i.user_id = :ownerId
            and b.ended < :now1
            order by b.started desc
            limit :size offset :from
            """, nativeQuery = true)
    List<Booking> findPastByOwnerItem(@Param("ownerId") long ownerId,
                                      @Param("now") LocalDateTime now,
                                      @Param("from") int from,
                                      @Param("size") int size);

    //FUTURE BY OWNER
    @Query(value = """
            select * from booking b
            join items i on b.item_id = i.id
            where i.user_id = :ownerId
            and b.started > :now
            order by b.started desc
            limit :size offset :from
            """, nativeQuery = true)
    List<Booking> findFutureByOwnerItem(@Param("ownerId") long ownerId,
                                        @Param("now") LocalDateTime now,
                                        @Param("from") int from,
                                        @Param("size") int size);

    //STATUS BY OWNER
    @Query(value = """
            select * from booking b
            join items i on b.item_id = i.id
            where i.user_id = :ownerId
            and b.status = :status
            order by b.started desc
            limit :size offset :from
            """, nativeQuery = true)
    List<Booking> findStatusByOwnerItem(@Param("ownerId") long ownerId,
                                        @Param("status") Status status,
                                        @Param("from") int from,
                                        @Param("size") int size);

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
