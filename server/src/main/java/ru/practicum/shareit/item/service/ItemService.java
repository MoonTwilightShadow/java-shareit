package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemWithBookingResponse getById(Integer id, Integer userId);

    List<ItemWithBookingResponse> getByOwner(Integer owner, Integer from, Integer size);

    List<ItemResponse> search(String text, Integer from, Integer size);

    ItemResponse create(CreateItemRequest request, Integer ownerId);

    ItemResponse update(UpdateItemRequest request, Integer itemId, Integer ownerId);

    void delete(Integer id);

    CommentResponse comment(Integer itemId, Integer userId, CommentRequest comment);
}
