package ru.practicum.shareit.request.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithItems;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public Request mapFromRequest(ItemRequestDto requestDto) {
        return new Request(requestDto.getDescription());
    }

    public ItemRequestResponse mapToResponse(Request request) {
        return new ItemRequestResponse(
                request.getId(),
                request.getDescription(),
                request.getCreated()
        );
    }

    public ItemRequestWithItems mapToResponseWithItems(Request request, List<Item> items) {
        return new ItemRequestWithItems(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                items.stream()
                        .map(ItemMapper::mapToInfo)
                        .collect(Collectors.toList())
        );
    }
}
