package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getById(Integer id);

    List<Item> getByOwner(Integer owner);

    List<Item> search(String text);

    Item create(CreateItemRequest request, Integer ownerId);

    Item update(UpdateItemRequest request);

    void delete(Integer id);
}
