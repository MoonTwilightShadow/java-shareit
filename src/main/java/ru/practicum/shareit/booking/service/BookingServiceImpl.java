package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.StatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponse create(BookingRequest request, Integer bookerId) {
        log.info("create method booking");

        LocalDateTime start = request.getStart();
        LocalDateTime end = request.getEnd();
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new IllegalArgumentException();
        }

        Booking booking = BookingMapper.mapFromRequest(request);

        Item item = itemRepository.findById(request.getItemId()).orElseThrow(NotFoundException::new);
        if (!item.getAvailable()) {
            throw new IllegalArgumentException();
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException();
        }

        booking.setItem(item);
        booking.setBooker(userRepository.findById(bookerId).orElseThrow(NotFoundException::new));
        booking.setStatus(Status.WAITING);

        return BookingMapper.mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse approve(Integer ownerId, Integer bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(NotFoundException::new);

        if (!Objects.equals(booking.getItem().getOwner().getId(), ownerId)) {
            throw new NotFoundException();
        }

        if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED) || booking.getStatus().equals(Status.CANCELED)) {
            throw new IllegalArgumentException();
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        return BookingMapper.mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse getById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(NotFoundException::new);

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException();
        }

        return BookingMapper.mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getAllByBooker(Integer bookerId, String state, Integer from, Integer size) {
        if (userRepository.findById(bookerId).isEmpty()) {
            throw new NotFoundException();
        }

        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException();
        }

        PageRequest page = PageRequest.of(from / size, size);
        Page<Booking> bookings;
        LocalDateTime current = LocalDateTime.now();

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findBookingsByBooker_IdOrderByStartDesc(bookerId, page);
                break;
            case "CURRENT":
                bookings = bookingRepository.findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartAsc(bookerId, current, current, page);
                break;
            case "PAST":
                bookings = bookingRepository.findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(bookerId, current, page);
                break;
            case "FUTURE":
                bookings = bookingRepository.findBookingsByBooker_IdAndStartAfterOrderByStartDesc(bookerId, current, page);
                break;
            case "WAITING":
                bookings = bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(bookerId, Status.WAITING, page);
                break;
            case "REJECTED":
                bookings = bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(bookerId, Status.REJECTED, page);
                break;
            default:
                throw new StatusException(state);
        }

        return bookings.stream()
                .map(BookingMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getAllByOwner(Integer ownerId, String state, Integer from, Integer size) {
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException();
        }

        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException();
        }

        PageRequest page = PageRequest.of(from / size, size);
        Page<Booking> bookings;
        LocalDateTime current = LocalDateTime.now();

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findBookingsByItemOwnerIdOrderByStartDesc(ownerId, page);
                break;
            case "CURRENT":
                bookings = bookingRepository.findBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, current, current, page);
                break;
            case "PAST":
                bookings = bookingRepository.findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, current, page);
                break;
            case "FUTURE":
                bookings = bookingRepository.findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, current, page);
                break;
            case "WAITING":
                bookings = bookingRepository.findBookingsByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING, page);
                break;
            case "REJECTED":
                bookings = bookingRepository.findBookingsByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED, page);
                break;
            default:
                throw new StatusException(state);
        }

        System.out.println(bookings);

        return bookings.stream()
                .map(BookingMapper::mapToResponse)
                .collect(Collectors.toList());
    }
}
