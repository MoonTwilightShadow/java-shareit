package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    Page<Item> findItemsByOwnerIdOrderByIdAsc(Integer ownerId, Pageable page);

    Page<Item> findItemsByNameOrDescriptionContainsIgnoreCaseAndAvailableTrue(String name, String description, Pageable page);

    List<Item> findItemsByRequestId(Integer id);
}
