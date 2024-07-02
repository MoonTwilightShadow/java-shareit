package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemWithBookingResponse getById(Integer id, Integer userId);

    List<ItemWithBookingResponse> getByOwner(Integer owner);

    List<ItemResponse> search(String text);

    Item create(CreateItemRequest request, Integer ownerId);

    ItemResponse update(UpdateItemRequest request, Integer itemId, Integer ownerId);

    void delete(Integer id);

    CommentResponse comment(Integer itemId, Integer userId, String text);
}
