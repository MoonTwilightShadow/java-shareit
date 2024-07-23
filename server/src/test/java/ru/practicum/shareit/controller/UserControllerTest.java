package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private User user;
    private UserRequest ur;
    private UserRequest ur2;

    @BeforeEach
    void setUp() {
        user = new User(
                1,
                "User",
                "mail@mail.ru"

        );

        ur = new UserRequest(
                "User",
                "mail@mail.ru"
        );

        ur2 = new UserRequest(
                "User2",
                "mail2@mail.ru"
        );
    }

    @Test
    public void testCreate() throws Exception {
        when(userService.create(any()))
                .thenReturn(user);

        mvc.perform(
                        post("/users")
                                .content(mapper.writeValueAsString(ur))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(ur.getName())))
                .andExpect(jsonPath("$.email", is(ur.getEmail())));
    }

    @Test
    public void testCreateNameFailed() throws Exception {
        when(userService.create(any()))
                .thenReturn(user);

        UserRequest userFailed = new UserRequest(
                "",
                "email@box.ru"
        );

        mvc.perform(
                        post("/users")
                                .content(mapper.writeValueAsString(userFailed))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testCreateEmailFailed() throws Exception {
        when(userService.create(any()))
                .thenReturn(user);

        UserRequest userFailed = new UserRequest(
                "Name",
                "email"
        );

        mvc.perform(
                        post("/users")
                                .content(mapper.writeValueAsString(userFailed))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetById() throws Exception {
        when(userService.getById(any()))
                .thenReturn(user);

        mvc.perform(
                        get("/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    public void testGetAllEmpty() throws Exception {
        when(userService.getAll())
                .thenReturn(Collections.emptyList());

        mvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetAll() throws Exception {
        User user2 = new User(
                2,
                "name",
                "email2@box.ru"
        );

        when(userService.getAll())
                .thenReturn(List.of(user, user2));

        mvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(user.getName())))
                .andExpect(jsonPath("$.[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$.[1].id", is(user2.getId()), Integer.class))
                .andExpect(jsonPath("$.[1].name", is(user2.getName())))
                .andExpect(jsonPath("$.[1].email", is(user2.getEmail())));
    }

    @Test
    public void testUpdate() throws Exception {
        when(userService.update(any(), anyInt()))
                .thenReturn(user);

        mvc.perform(
                        patch("/users/1")
                                .content(mapper.writeValueAsString(ur))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    public void testDelete() throws Exception {
        mvc.perform(
                        delete("/users/1")
                )
                .andExpect(status().isOk());
    }
}
