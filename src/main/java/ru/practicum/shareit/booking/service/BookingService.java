package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse create(BookingRequest request, Integer bookerId);

    BookingResponse approve(Integer ownerId, Integer bookingId, Boolean approved);

    BookingResponse getById(Integer bookingId, Integer userId);

    List<BookingResponse> getAllByBooker(Integer bookerId, String state);

    List<BookingResponse> getAllByOwner(Integer ownerId, String state);
}
