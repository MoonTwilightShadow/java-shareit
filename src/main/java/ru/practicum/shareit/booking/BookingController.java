package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponse create(@Valid @RequestBody BookingRequest request, @RequestHeader(userIdHeader) Integer bookerId) {
        return bookingService.create(request, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse approve(@RequestHeader(userIdHeader) Integer ownerId, @PathVariable Integer bookingId, @RequestParam(value = "approved") Boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getById(@PathVariable Integer bookingId, @RequestHeader(userIdHeader) Integer userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponse> getAllByBooker(@RequestHeader(userIdHeader) Integer bookerId, @Valid @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getAllByOwner(@RequestHeader(userIdHeader) Integer ownerId, @Valid @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllByOwner(ownerId, state);
    }
}