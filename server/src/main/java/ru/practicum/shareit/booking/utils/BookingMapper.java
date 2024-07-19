package ru.practicum.shareit.booking.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingShortResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.utils.UserMapper;

@UtilityClass
public class BookingMapper {
    public Booking mapFromRequest(BookingRequest request) {
        return new Booking(
                request.getStart(),
                request.getEnd()
        );
    }

    public BookingResponse mapToResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                ItemMapper.mapToResponse(booking.getItem()),
                UserMapper.mapToResponse(booking.getBooker())
        );
    }

    public BookingShortResponse mapToShortResponse(Booking booking) {
        return new BookingShortResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId()
        );
    }
}
