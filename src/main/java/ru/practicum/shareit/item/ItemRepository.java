package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByUserId(long userId);

    boolean existsByIdAndUserId(long itemId, long userId);

    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String desc);
}
