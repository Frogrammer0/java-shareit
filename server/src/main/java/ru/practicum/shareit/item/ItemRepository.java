package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(long ownerId);

    boolean existsByIdAndOwnerId(long itemId, long ownerId);

    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String name,
                                                                                                    String desc);

    boolean existsByIdAndAvailableIsTrue(long id);

    boolean existsByOwnerId(long ownerId);
}
