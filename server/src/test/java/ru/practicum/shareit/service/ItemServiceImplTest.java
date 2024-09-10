package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.NotOwnerException;
import ru.practicum.shareit.exception.exceptions.OwnerExeption;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private Comment comment;

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
        booking.setStatus(Status.APPROVED);

        comment = new Comment();
        comment.setId(1);
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setText("Nice");
    }

    @Test
    public void testGetByIdNotFoundItem() {
        assertThrows(NotFoundException.class,
                () -> itemService.getById(1, 1));

        verify(bookingRepository, never()).findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(any(LocalDateTime.class), any(Status.class), anyInt());
        verify(bookingRepository, never()).findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(any(LocalDateTime.class), any(Status.class), anyInt());
        verify(commentRepository, never()).findCommentsByItemId(anyInt());
    }

    @Test
    public void testGetItemByIdNotOwner() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(commentRepository.findCommentsByItemId(anyInt())).thenReturn(List.of(comment));

        ItemWithBookingResponse saveItem = itemService.getById(1, 2);

        assertNull(saveItem.getLastBooking());
        assertNull(saveItem.getNextBooking());
        assertThat(1, equalTo(saveItem.getComments().size()));

        verify(bookingRepository, never()).findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(any(LocalDateTime.class), any(Status.class), anyInt());
        verify(bookingRepository, never()).findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(any(LocalDateTime.class), any(Status.class), anyInt());
        verify(commentRepository).findCommentsByItemId(anyInt());
    }

    @Test
    public void testGetItemById() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(any(LocalDateTime.class), any(Status.class), anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(any(LocalDateTime.class), any(Status.class), anyInt())).thenReturn(Optional.of(booking));
        when(commentRepository.findCommentsByItemId(anyInt())).thenReturn(List.of());

        ItemWithBookingResponse savedItem = itemService.getById(1, 1);

        assertThat(savedItem.getId(), equalTo(item.getId()));
        assertThat(savedItem.getName(), equalTo(item.getName()));
        assertThat(savedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(savedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(savedItem.getOwnerId(), equalTo(item.getOwner().getId()));
        assertThat(savedItem.getLastBooking(), equalTo(BookingMapper.mapToShortResponse(booking)));
        assertThat(savedItem.getNextBooking(), equalTo(BookingMapper.mapToShortResponse(booking)));
    }

    @Test
    public void testGetByOwnerNotFoundOwner() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotOwnerException.class,
                () -> itemService.getByOwner(10, 0, 10));

        verify(itemRepository, never()).findItemsByOwnerIdOrderByIdAsc(anyInt(), any(Pageable.class));
        verify(bookingRepository, never()).findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(any(LocalDateTime.class), any(Status.class), anyInt());
        verify(bookingRepository, never()).findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(any(LocalDateTime.class), any(Status.class), anyInt());
        verify(commentRepository, never()).findCommentsByItemId(anyInt());
    }

    @Test
    public void testGetByOwnerFromSize() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));

        assertThrows(IllegalArgumentException.class,
                () -> itemService.getByOwner(1, -2, 10));
        assertThrows(IllegalArgumentException.class,
                () -> itemService.getByOwner(1, 2, 0));
        assertThrows(IllegalArgumentException.class,
                () -> itemService.getByOwner(1, 1, -10));


        verify(itemRepository, never()).findItemsByOwnerIdOrderByIdAsc(anyInt(), any(Pageable.class));
        verify(bookingRepository, never()).findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(any(LocalDateTime.class), any(Status.class), anyInt());
        verify(bookingRepository, never()).findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(any(LocalDateTime.class), any(Status.class), anyInt());
        verify(commentRepository, never()).findCommentsByItemId(anyInt());
    }

    @Test
    public void testGetByOwnerEmpty() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(itemRepository.findItemsByOwnerIdOrderByIdAsc(anyInt(), any(Pageable.class))).thenReturn(Page.empty());

        List<ItemWithBookingResponse> items = itemService.getByOwner(owner.getId(), 0, 10);

        assertEquals(0, items.size());

        verify(bookingRepository, never()).findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(any(LocalDateTime.class), any(Status.class), anyInt());
        verify(bookingRepository, never()).findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(any(LocalDateTime.class), any(Status.class), anyInt());
        verify(commentRepository, never()).findCommentsByItemId(anyInt());
    }

    @Test
    public void testGetByOwner() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        Page<Item> page = new PageImpl<>(List.of(item));
        when(itemRepository.findItemsByOwnerIdOrderByIdAsc(anyInt(), any(Pageable.class))).thenReturn(page);
        when(bookingRepository.findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(any(LocalDateTime.class), any(Status.class), anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(any(LocalDateTime.class), any(Status.class), anyInt())).thenReturn(Optional.of(booking));
        when(commentRepository.findCommentsByItemId(anyInt())).thenReturn(List.of(comment));

        List<ItemWithBookingResponse> items = itemService.getByOwner(owner.getId(), 0, 10);

        assertEquals(1, items.size());

        verify(bookingRepository).findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(any(LocalDateTime.class), any(Status.class), anyInt());
        verify(bookingRepository).findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(any(LocalDateTime.class), any(Status.class), anyInt());
        verify(commentRepository).findCommentsByItemId(anyInt());
    }

    @Test
    public void testSearchEmptyText() {
        assertEquals(0, itemService.search("", 0, 10).size());

        verify(itemRepository, never()).findItemsByNameOrDescriptionContainsIgnoreCaseAndAvailableTrue(anyString(), anyString(), any());
    }

    @Test
    public void testSearchFromSize() {
        assertThrows(IllegalArgumentException.class,
                () -> itemService.search("item", 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> itemService.search("item", -1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> itemService.search("item", -10, -10));

        verify(itemRepository, never()).findItemsByNameOrDescriptionContainsIgnoreCaseAndAvailableTrue(anyString(), anyString(), any());
    }

    @Test
    public void testSearchEmpty() {
        when(itemRepository.findItemsByNameOrDescriptionContainsIgnoreCaseAndAvailableTrue(anyString(), anyString(), any()))
                .thenReturn(Page.empty());

        assertEquals(0, itemService.search("item", 0, 10).size());
    }

    @Test
    public void testSearch() {
        Page<Item> page = new PageImpl<>(List.of(item));

        when(itemRepository.findItemsByNameOrDescriptionContainsIgnoreCaseAndAvailableTrue(anyString(), anyString(), any()))
                .thenReturn(page);

        List<ItemResponse> saved = itemService.search("item", 0, 10);

        assertEquals(1, saved.size());
        assertEquals(page.getContent().get(0).getId(), saved.get(0).getId());
        assertEquals(page.getContent().get(0).getName(), saved.get(0).getName());
    }

    @Test
    public void testCreateNotFoundOwner() {
        assertThrows(NotOwnerException.class,
                () -> itemService.create(new CreateItemRequest("name", "description", true, null), 10));

        verify(requestRepository, never()).findById(anyInt());
        verify(itemRepository, never()).save(any());
    }

    @Test
    public void testCreateWithoutRequest() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(itemRepository.save(any()))
                .thenReturn(item);

        CreateItemRequest ir = new CreateItemRequest("name", "description", true, null);
        ItemResponse saved = itemService.create(ir, owner.getId());

        assertEquals(item.getId(), saved.getId());
        assertEquals(item.getName(), saved.getName());

        verify(requestRepository, never()).findById(anyInt());
    }

    @Test
    public void testCreatedWithRequest() {
        Request request = new Request(
                1,
                "description",
                booker,
                LocalDateTime.now()
        );

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(requestRepository.findById(any()))
                .thenReturn(Optional.of(request));
        when(itemRepository.save(any()))
                .thenReturn(item);

        item.setRequest(request);
        CreateItemRequest ir = new CreateItemRequest("Item", "Description", true, 1);
        ItemResponse saved = itemService.create(ir, owner.getId());

        assertEquals(item.getId(), saved.getId());
        assertEquals(item.getName(), saved.getName());
        assertEquals(item.getRequest().getId(), saved.getRequestId());
    }

    @Test
    public void testUpdateItemNotFound() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.update(new UpdateItemRequest("name", "desc", true), 1, 1));

        verify(itemRepository, never()).save(any());
    }

    @Test
    public void testUpdateWithOwnerException() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(OwnerExeption.class,
                () -> itemService.update(new UpdateItemRequest("name", "desc", true), 1, 5));

        verify(itemRepository, never()).save(any());
    }

    @Test
    public void addCommentItemNotFound() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.comment(1, 1, new CommentRequest("text")));

        verify(userRepository, never()).findById(anyInt());
        verify(bookingRepository, never()).findCompletedBooking(anyInt(), anyInt());
        verify(commentRepository, never()).save(any());
    }

    @Test
    public void addCommentUserNotFound() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.comment(1, 1, new CommentRequest("text")));

        verify(bookingRepository, never()).findCompletedBooking(anyInt(), anyInt());
        verify(commentRepository, never()).save(any());
    }

    @Test
    public void addCommentBookingNotFound() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findCompletedBooking(anyInt(), anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> itemService.comment(1, 1, new CommentRequest("text")));

        verify(commentRepository, never()).save(any());
    }

    @Test
    public void addCommenEmptyText() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findCompletedBooking(anyInt(), anyInt())).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> itemService.comment(1, 1, new CommentRequest("  ")));

        verify(commentRepository, never()).save(any());
    }





































}
