package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {
    private Map<Integer, Item> items = new HashMap<>();
    int nextId = 1;

    @Override
    public Item getById(Integer id) {
        return items.getOrDefault(id, null);
    }

    @Override
    public List<Item> getByOwner(Integer owner) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner(), owner))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                                item.getDescription().toLowerCase().contains(text))
                .filter(Item::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        item.setId(nextId++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Integer id) {
        items.remove(id);
    }
}