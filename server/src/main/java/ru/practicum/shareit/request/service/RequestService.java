package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithItems;

import java.util.List;

public interface RequestService {
    ItemRequestResponse create(ItemRequestDto request, Integer requestorId);

    List<ItemRequestWithItems> getRequestsOwner(Integer requestorId);

    List<ItemRequestWithItems> getRequests(Integer from, Integer size, Integer userId);

    ItemRequestWithItems getRequestById(Integer requestId, Integer userId);
}
