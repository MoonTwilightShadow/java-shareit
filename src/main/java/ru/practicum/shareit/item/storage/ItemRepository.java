package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findItemsByOwner(Integer owner);

    List<Item> findItemsByNameOrDescriptionContainsIgnoreCaseAndAvailableTrue(String name, String description);
}
