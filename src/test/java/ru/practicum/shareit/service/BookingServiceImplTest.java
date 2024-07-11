package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.StatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private Booking booking;
    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setName("User");
        owner.setEmail("mail@mail.ru");

        booker = new User();
        booker.setId(2);
        booker.setName("Booker");
        booker.setEmail("booker@mail.ru");

        item = new Item();
        item.setId(1);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
    }

    @Test
    public void testCreateStartEndFailed() {
        BookingRequest br = new BookingRequest(
                1,
                LocalDateTime.of(2024, 7, 8,10, 0, 0),
                LocalDateTime.of(2024, 7, 8,9, 0, 0)
        );

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.create(br, booker.getId()));

        br.setStart(LocalDateTime.of(2024, 7, 8,10, 0, 0));
        br.setEnd(LocalDateTime.of(2024, 7, 8,10, 0, 0));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.create(br, booker.getId()));

        verify(itemRepository, never()).findById(anyInt());
        verify(userRepository, never()).findById(anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void testCreateItemNotFound() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        BookingRequest br = new BookingRequest(
                1,
                LocalDateTime.of(2024, 7, 8,10, 0, 0),
                LocalDateTime.of(2024, 7, 8,11, 0, 0)
        );

        assertThrows(NotFoundException.class,
                () -> bookingService.create(br, booker.getId()));

        verify(userRepository, never()).findById(anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void testCreateItemUnavailable() {
        item.setAvailable(false);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        BookingRequest br = new BookingRequest(
                1,
                LocalDateTime.of(2024, 7, 8,10, 0, 0),
                LocalDateTime.of(2024, 7, 8,11, 0, 0)
        );

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.create(br, booker.getId()));

        verify(userRepository, never()).findById(anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void testCreateOwnerBookOwnItem() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        BookingRequest br = new BookingRequest(
                1,
                LocalDateTime.of(2024, 7, 8,10, 0, 0),
                LocalDateTime.of(2024, 7, 8,11, 0, 0)
        );

        assertThrows(NotFoundException.class,
                () -> bookingService.create(br, owner.getId()));

        verify(userRepository, never()).findById(anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void testCreateUserNotFound() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        BookingRequest br = new BookingRequest(
                1,
                LocalDateTime.of(2024, 7, 8,10, 0, 0),
                LocalDateTime.of(2024, 7, 8,11, 0, 0)
        );

        assertThrows(NotFoundException.class,
                () -> bookingService.create(br, booker.getId()));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void testCreate() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingRequest br = new BookingRequest(
                1,
                LocalDateTime.of(2024, 7, 8,10, 0, 0),
                LocalDateTime.of(2024, 7, 8,11, 0, 0)
        );

        BookingResponse saved = bookingService.create(br, booker.getId());
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());
    }

    @Test
    public void testApproveBookingNotFound() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.approve(owner.getId(), booking.getId(), true));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void testApproveNotOwner() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class,
                () -> bookingService.approve(5, booking.getId(), true));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void testApproveStatusNotWaiting() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.approve(owner.getId(), booking.getId(), true));

        booking.setStatus(Status.REJECTED);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.approve(owner.getId(), booking.getId(), true));

        booking.setStatus(Status.CANCELED);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.approve(owner.getId(), booking.getId(), true));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void testGetBookingByIdNotFound() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(booking.getId(), owner.getId()));
    }

    @Test
    public void testGetBookingByIdUnknownUserId() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getById(booking.getId(), 99));
    }

    @Test
    public void testGetBookingByIdOwner() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));

        BookingResponse saved = bookingService.getById(booking.getId(), owner.getId());
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());
    }

    @Test
    public void testGetBookingByIdBooker() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.ofNullable(booking));

        BookingResponse saved = bookingService.getById(booking.getId(), booker.getId());
        BookingResponse expected = BookingMapper.mapToResponse(booking);
        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());
    }

    @Test
    public void testGetAllByBookerUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> bookingService.getAllByBooker(99, "ALL", 0, 10));
    }

    @Test
    public void testGetAllByBookerFromSizeFailed() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllByBooker(booker.getId(), "ALL", -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllByBooker(booker.getId(), "ALL", -1, 0));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllByBooker(booker.getId(), "ALL", 0, -1));
    }

    @Test
    public void testGetUserBookingsUnknownStatus() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));

        assertThrows(StatusException.class,
                () -> bookingService.getAllByBooker(booker.getId(), "Unknown", 0, 10));
    }

    @Test
    public void testGetAllByBookerAll() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBooker_IdOrderByStartDesc(anyInt(), any())).thenReturn(page);

        BookingResponse saved = bookingService.getAllByBooker(booker.getId(), "ALL", 0, 10).get(0);
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());

        verify(bookingRepository, never()).findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartAsc(anyInt(), any(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStartAfterOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStatusOrderByStartDesc(anyInt(), any(), any());
    }

    @Test
    public void testGetAllByBookerCurrent() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartAsc(anyInt(), any(), any(), any())).thenReturn(page);

        BookingResponse saved = bookingService.getAllByBooker(booker.getId(), "CURRENT", 0, 10).get(0);
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());

        verify(bookingRepository, never()).findBookingsByBooker_IdOrderByStartDesc(anyInt(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStartAfterOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStatusOrderByStartDesc(anyInt(), any(), any());
    }

    @Test
    public void testAllByBookerPast() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(anyInt(), any(), any())).thenReturn(page);

        BookingResponse saved = bookingService.getAllByBooker(booker.getId(), "PAST", 0, 10).get(0);
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());

        verify(bookingRepository, never()).findBookingsByBooker_IdOrderByStartDesc(anyInt(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartAsc(anyInt(), any(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStartAfterOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStatusOrderByStartDesc(anyInt(), any(), any());
    }

    @Test
    public void testAllByBookerFuture() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBooker_IdAndStartAfterOrderByStartDesc(anyInt(), any(), any())).thenReturn(page);

        BookingResponse saved = bookingService.getAllByBooker(booker.getId(), "FUTURE", 0, 10).get(0);
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());

        verify(bookingRepository, never()).findBookingsByBooker_IdOrderByStartDesc(anyInt(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartAsc(anyInt(), any(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStatusOrderByStartDesc(anyInt(), any(), any());
    }

    @Test
    public void testGetAllByBookerWaiting() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(anyInt(), any(), any())).thenReturn(page);

        BookingResponse saved = bookingService.getAllByBooker(booker.getId(), "WAITING", 0, 10).get(0);
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());

        verify(bookingRepository, never()).findBookingsByBooker_IdOrderByStartDesc(anyInt(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartAsc(anyInt(), any(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStartAfterOrderByStartDesc(anyInt(), any(), any());
    }

    @Test
    public void testAllByBookerRejected() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(anyInt(), any(), any())).thenReturn(page);

        BookingResponse saved = bookingService.getAllByBooker(booker.getId(), "REJECTED", 0, 10).get(0);
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());

        verify(bookingRepository, never()).findBookingsByBooker_IdOrderByStartDesc(anyInt(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartAsc(anyInt(), any(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByBooker_IdAndStartAfterOrderByStartDesc(anyInt(), any(), any());
    }

    @Test
    public void testGetAllByOwnerUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> bookingService.getAllByOwner(99, "ALL", 0, 10));
    }

    @Test
    public void testGetAllByOwnerFromSizeFailed() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllByOwner(booker.getId(), "ALL", -1, 10));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllByOwner(booker.getId(), "ALL", -1, 0));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllByOwner(booker.getId(), "ALL", 0, -1));
    }

    @Test
    public void testGetAllByOwnerUnknownStatus() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));

        assertThrows(StatusException.class,
                () -> bookingService.getAllByOwner(booker.getId(), "Unknown", 0, 10));
    }

    @Test
    public void testGetAllByOwnerAll() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByItemOwnerIdOrderByStartDesc(anyInt(), any())).thenReturn(page);

        BookingResponse saved = bookingService.getAllByOwner(booker.getId(), "ALL", 0, 10).get(0);
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());

        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyInt(), any(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(), any());
    }

    @Test
    public void testGetAllByOwnerCurrent() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyInt(), any(), any(), any())).thenReturn(page);

        BookingResponse saved = bookingService.getAllByOwner(booker.getId(), "CURRENT", 0, 10).get(0);
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());

        verify(bookingRepository, never()).findBookingsByItemOwnerIdOrderByStartDesc(anyInt(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(), any());
    }

    @Test
    public void testGetAllByOwnerPast() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(anyInt(), any(), any())).thenReturn(page);

        BookingResponse saved = bookingService.getAllByOwner(booker.getId(), "PAST", 0, 10).get(0);
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());

        verify(bookingRepository, never()).findBookingsByItemOwnerIdOrderByStartDesc(anyInt(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyInt(), any(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(), any());
    }

    @Test
    public void testGetAllByOwnerFuture() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(anyInt(), any(), any())).thenReturn(page);

        BookingResponse saved = bookingService.getAllByOwner(booker.getId(), "FUTURE", 0, 10).get(0);
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());

        verify(bookingRepository, never()).findBookingsByItemOwnerIdOrderByStartDesc(anyInt(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyInt(), any(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(), any());
    }

    @Test
    public void testGetAllByOwnerWaiting() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(), any())).thenReturn(page);

        BookingResponse saved = bookingService.getAllByOwner(booker.getId(), "WAITING", 0, 10).get(0);
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());

        verify(bookingRepository, never()).findBookingsByItemOwnerIdOrderByStartDesc(anyInt(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyInt(), any(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(anyInt(), any(), any());
    }

    @Test
    public void testGetAllByOwnerRejected() {
        final Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(), any())).thenReturn(page);

        BookingResponse saved = bookingService.getAllByOwner(booker.getId(), "REJECTED", 0, 10).get(0);
        BookingResponse expected = BookingMapper.mapToResponse(booking);

        assertEquals(expected.getId(), saved.getId());
        assertEquals(expected.getBooker().getId(), saved.getBooker().getId());
        assertEquals(expected.getItem().getId(), saved.getItem().getId());

        verify(bookingRepository, never()).findBookingsByItemOwnerIdOrderByStartDesc(anyInt(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyInt(), any(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(anyInt(), any(), any());
        verify(bookingRepository, never()).findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(anyInt(), any(), any());
    }
}
