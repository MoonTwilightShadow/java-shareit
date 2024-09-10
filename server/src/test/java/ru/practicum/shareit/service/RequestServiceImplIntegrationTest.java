package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithItems;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestServiceImplIntegrationTest {
    @Autowired
    private RequestService requestService;
    @Autowired
    private UserService userService;

    private UserRequest ur;
    private UserRequest ur2;
    private ItemRequestDto ird;

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

        ird = new ItemRequestDto(
                "Description"
        );
    }

    @Test
    public void testCreate() {
        int urId = userService.create(ur).getId();
        ItemRequestResponse resp = requestService.create(ird, urId);

        assertThat(resp.getId(), notNullValue());
        assertThat(resp.getDescription(), equalTo(ird.getDescription()));
        assertThat(resp.getCreated(), any(LocalDateTime.class));
    }

    @Test
    public void testGetRequestOwner() {
        int urId = userService.create(ur).getId();
        ItemRequestResponse resp = requestService.create(ird, urId);

        List<ItemRequestWithItems> requests = requestService.getRequestsOwner(urId);

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getId(), equalTo(resp.getId()));
    }

    @Test void testGetRequestsEmpty() {
        int urId = userService.create(ur).getId();

        ItemRequestResponse resp = requestService.create(ird, urId);

        List<ItemRequestWithItems> requests = requestService.getRequests(0, 10, urId);

        assertThat(requests.size(), equalTo(0));
    }

    @Test
    void testGetRequests() {
        int urId = userService.create(ur).getId();
        int urId2 = userService.create(ur2).getId();

        ItemRequestResponse resp = requestService.create(ird, urId);

        List<ItemRequestWithItems> requests = requestService.getRequests(0, 10, urId2);

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getId(), equalTo(resp.getId()));
    }

    @Test
    public void testGetRequestById() {
        int urId = userService.create(ur).getId();
        ItemRequestResponse resp = requestService.create(ird, urId);

        ItemRequestWithItems saved = requestService.getRequestById(resp.getId(), urId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        assertThat(saved.getId(), equalTo(resp.getId()));
        assertThat(saved.getDescription(), equalTo(resp.getDescription()));
        assertThat(saved.getCreated().format(formatter), equalTo(resp.getCreated().format(formatter)));
        assertThat(saved.getItems().size(), equalTo(0));
    }
}
