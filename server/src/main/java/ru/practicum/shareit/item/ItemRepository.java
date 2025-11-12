package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {


    @Query(value = """
            select * from items i
            where i.user_id = :ownerId
            order by i.id desc
            limit :size offset :from
            """, nativeQuery = true)
    List<Item> findAllByOwnerId(long ownerId, int from, int size);

    boolean existsByIdAndOwnerId(long itemId, long ownerId);

    @Query(value = """
        select *
        from items i
        where i.available = TRUE
          and (lower(i.name) like lower(concat('%', :query, '%'))
               or lower(i.description) like lower(concat('%', :query, '%')))
        order by i.id asc
        limit :size offset :from
        """, nativeQuery = true)
    List<Item> searchAvailableItems(@Param("query") String query,
                                    @Param("from") int from,
                                    @Param("size") int size);


    List<Item> findAllByRequestId(long requestId);

    List<Item> findAllByRequestIdIn(List<Long> itemsId);

    boolean existsByIdAndAvailableIsTrue(long id);

    boolean existsByOwnerId(long ownerId);
}
