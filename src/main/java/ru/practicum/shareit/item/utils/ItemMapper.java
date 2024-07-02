package ru.practicum.shareit.item.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingShortResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemWithBookingResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

    public Item mapFromRequest(CreateItemRequest request) {
        return new Item(
                request.getName(),
                request.getDescription(),
                request.getAvailable()
        );
    }

    public ItemResponse mapToResponse(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequest() == null ? null : item.getRequest().getId()
        );
    }

    public ItemWithBookingResponse mapToBookingResponse(Item item) {
        return new ItemWithBookingResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                null,
                null
        );
    }

    public CommentResponse mapToCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
