package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceImplIntegerationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;

    private UserRequest ur;
    private UserRequest ur2;
    private CreateItemRequest ir;
    private CreateItemRequest ir2;

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

        ir2 = new CreateItemRequest(
                "Item2",
                "Description2",
                true,
                null
        );
    }

    @Test
    public void testCreate() {
        User user = userService.create(ur);
        ItemResponse item = itemService.create(ir, user.getId());

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(ir.getName()));
        assertThat(item.getDescription(), equalTo(ir.getDescription()));
        assertThat(item.getAvailable(), equalTo(ir.getAvailable()));
        assertThat(item.getRequestId(), nullValue());
    }

    @Test
    public void testGetById() {
        User user = userService.create(ur);
        int id = itemService.create(ir, user.getId()).getId();

        ItemWithBookingResponse item = itemService.getById(id, user.getId());
        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(ir.getName()));
        assertThat(item.getDescription(), equalTo(ir.getDescription()));
        assertThat(item.getAvailable(), equalTo(ir.getAvailable()));
        assertThat(item.getLastBooking(), nullValue());
        assertThat(item.getNextBooking(), nullValue());
        assertThat(item.getComments().size(), equalTo(0));
    }

    @Test
    public void testGetByOwner() {
        User user = userService.create(ur);
        int id1 = itemService.create(ir, user.getId()).getId();
        int id2 = itemService.create(ir2, user.getId()).getId();

        List<ItemWithBookingResponse> items = itemService.getByOwner(user.getId(), 0, 10);
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(0).getId(), equalTo(id1));
        assertThat(items.get(1).getId(), equalTo(id2));
    }

    @Test
    public void testSearch() {
        User user = userService.create(ur);
        int id1 = itemService.create(ir, user.getId()).getId();
        int id2 = itemService.create(ir2, user.getId()).getId();

        List<ItemResponse> items = itemService.search("Item", 0, 10);
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(id1));
    }

    @Test
    public void testUpdate() {
        User user = userService.create(ur);
        int id = itemService.create(ir, user.getId()).getId();

        UpdateItemRequest uir = new UpdateItemRequest("updName", "updDesc", true);
        ItemResponse item = itemService.update(uir, id, user.getId());

        assertThat(item.getId(), equalTo(id));
        assertThat(item.getName(), equalTo(uir.getName()));
        assertThat(item.getDescription(), equalTo(uir.getDescription()));
        assertThat(item.getAvailable(), equalTo(uir.getAvailable()));
        assertThat(item.getOwnerId(), equalTo(user.getId()));
        assertThat(item.getRequestId(), nullValue());
    }

    @Test
    public void testDelete() {
        User user = userService.create(ur);
        int id = itemService.create(ir, user.getId()).getId();

        itemService.delete(id);
        assertThrows(NotFoundException.class,
                () -> itemService.getById(id, user.getId()));
    }

    @Test
    @Transactional
    public void testComment() {
        User user = userService.create(ur);
        User user2 = userService.create(ur2);
        int id1 = itemService.create(ir, user.getId()).getId();
        int bId = bookingService.create(new BookingRequest(
                id1,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        ), user2.getId()).getId();
        bookingService.approve(user.getId(), bId, true);

        CommentResponse cr = itemService.comment(id1, user2.getId(), new CommentRequest("text"));

        assertThat(cr.getId(), notNullValue());
        assertThat(cr.getText(), equalTo("text"));
        assertThat(cr.getAuthorName(), equalTo(user2.getName()));
        assertThat(cr.getCreated(), notNullValue());
    }
}
