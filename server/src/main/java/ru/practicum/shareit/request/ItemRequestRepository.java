package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query(value = """
            select * from requests r
            where r.requestor_id = :requestorId
            order by r.created desc
            """, nativeQuery = true)
    List<ItemRequest> findAllByRequestorId(@Param("requestorId") long requestorId);

    @Query(value = """
            select * from requests r
            order by r.created desc
            limit :size offset :from
            """, nativeQuery = true)
    List<ItemRequest> findAllRequests(@Param("from") int from,
                                      @Param("size") int size);
}
