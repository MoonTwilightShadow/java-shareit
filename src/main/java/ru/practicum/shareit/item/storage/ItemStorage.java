package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item getById(Integer id);

    List<Item> getByOwner(Integer owner);

    List<Item> search(String text);

    Item create(Item item);

    Item update(Item item);

    void delete(Item item);
}
