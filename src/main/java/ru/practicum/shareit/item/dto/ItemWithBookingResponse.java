package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortResponse;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBookingResponse {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer ownerId;
    private BookingShortResponse lastBooking;
    private BookingShortResponse nextBooking;
    private List<CommentResponse> comments;
}
