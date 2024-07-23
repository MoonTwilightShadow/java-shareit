package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    private UserRequest ur;
    private UserRequest ur2;
    private CreateItemRequest ir;
    private BookingRequest br;

    @BeforeEach
    void setUp() {
        ur = new UserRequest(
                "User",
                "mail@mail.ru"
        );

        ur2 = new UserRequest(
                "User2",
                "mail2@mail.ru"
        );

        ir = new CreateItemRequest(
                "Item",
                "Description",
                true,
                null
        );

        br = new BookingRequest(
                1,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );
    }

    @Test
    public void testCreate() {
        int userId = userService.create(ur).getId();
        int userId2 = userService.create(ur2).getId();
        int itemId = itemService.create(ir, userId).getId();

        BookingResponse booking = bookingService.create(br, userId2);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getItem().getId(), equalTo(itemId));
        assertThat(booking.getBooker().getId(), equalTo(userId2));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    @Transactional
    public void testApprove() {
        int userId = userService.create(ur).getId();
        int userId2 = userService.create(ur2).getId();
        int itemId = itemService.create(ir, userId).getId();

        BookingResponse booking = bookingService.create(br, userId2);

        bookingService.approve(userId, booking.getId(), true);

        booking = bookingService.getById(booking.getId(), userId2);

        assertThat(Status.APPROVED, equalTo(booking.getStatus()));
    }

    @Test
    @Transactional
    public void testGetById() {
        int userId = userService.create(ur).getId();
        int userId2 = userService.create(ur2).getId();
        int itemId = itemService.create(ir, userId).getId();

        BookingResponse booking = bookingService.create(br, userId2);
        BookingResponse saved = bookingService.getById(booking.getId(), userId2);

        assertThat(booking.getId(), equalTo(saved.getId()));
        assertThat(booking.getStart(), equalTo(saved.getStart()));
        assertThat(booking.getEnd(), equalTo(saved.getEnd()));
        assertThat(booking.getStatus(), equalTo(saved.getStatus()));
        assertThat(booking.getItem().getId(), equalTo(itemId));
        assertThat(booking.getBooker().getId(), equalTo(userId2));
    }

    @Test
    @Transactional
    public void testGetAllByBookerAll() {
        int userId = userService.create(ur).getId();
        int userId2 = userService.create(ur2).getId();
        int itemId = itemService.create(ir, userId).getId();

        BookingResponse booking = bookingService.create(br, userId2);

        List<BookingResponse> bookings = bookingService.getAllByBooker(userId2, "ALL", 0, 10);

        assertThat(1, equalTo(bookings.size()));
        assertThat(booking, equalTo(bookings.get(0)));
    }

    @Test
    @Transactional
    public void testGetAllByOwner() {
        int userId = userService.create(ur).getId();
        int userId2 = userService.create(ur2).getId();
        int itemId = itemService.create(ir, userId).getId();

        BookingResponse booking = bookingService.create(br, userId2);
        List<BookingResponse> bookings = bookingService.getAllByOwner(userId, "ALL", 0, 10);

        assertThat(1, equalTo(bookings.size()));
        assertThat(booking, equalTo(bookings.get(0)));
    }
}
