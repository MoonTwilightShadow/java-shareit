package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponse create(@Valid @RequestBody BookingRequest request, @RequestHeader(USER_ID_HEADER) Integer bookerId) {
        return bookingService.create(request, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse approve(@RequestHeader(USER_ID_HEADER) Integer ownerId, @PathVariable Integer bookingId, @RequestParam(value = "approved") Boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getById(@PathVariable Integer bookingId, @RequestHeader(USER_ID_HEADER) Integer userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponse> getAllByBooker(@RequestHeader(USER_ID_HEADER) Integer bookerId,
                                                @Valid @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return bookingService.getAllByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getAllByOwner(@RequestHeader(USER_ID_HEADER) Integer ownerId,
                                               @Valid @RequestParam(value = "state", defaultValue = "ALL") String state,
                                               @RequestParam(value = "from", defaultValue = "0") Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return bookingService.getAllByOwner(ownerId, state, from, size);
    }
}