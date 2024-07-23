package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.user.dto.UserResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom er = new EasyRandom();

    @Test
    public void testCreateFailedItemId() throws Exception {
        BookingRequest request = er.nextObject(BookingRequest.class);
        request.setItemId(null);

        mvc.perform(
                        post("/bookings")
                                .content(mapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateFailedStart() throws Exception {
        BookingRequest request = new BookingRequest(
                1,
                null,
                LocalDateTime.now()
        );

        mvc.perform(
                        post("/bookings")
                                .content(mapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        request = new BookingRequest(
                1,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now()
        );

        mvc.perform(
                        post("/bookings")
                                .content(mapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateFailedEnd() throws Exception {
        BookingRequest request = new BookingRequest(
                1,
                LocalDateTime.now(),
                null
        );

        mvc.perform(
                        post("/bookings")
                                .content(mapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        request = new BookingRequest(
                1,
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(2)
        );

        mvc.perform(
                        post("/bookings")
                                .content(mapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateWithoutUserId() throws Exception {
        mvc.perform(
                        post("/bookings")
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCreate() throws Exception {
         BookingResponse response = new BookingResponse(
                 1,
                 LocalDateTime.now().plusHours(1),
                 LocalDateTime.now().plusHours(2),
                 Status.WAITING,
                 er.nextObject(ItemResponse.class),
                 er.nextObject(UserResponse.class)
         );

         BookingRequest request = new BookingRequest(
                 1,
                 LocalDateTime.now().plusHours(1),
                 LocalDateTime.now().plusHours(2)
         );

        when(bookingService.create(any(), anyInt()))
                .thenReturn(response);

        mvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", "1")
                                .content(mapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Integer.class));
    }

    @Test
    public void testApproveWithoutUserId() throws Exception {
        mvc.perform(
                        patch("/bookings/1")
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testApprove() throws Exception {
        BookingResponse response = er.nextObject(BookingResponse.class);

        when(bookingService.approve(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(response);

        mvc.perform(
                        patch("/bookings/1?approved=true")
                                .header("X-Sharer-User-Id", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(response.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.booker.id", is(response.getBooker().getId()), Integer.class));
    }

    @Test
    public void testGetByIdWithoutUserId() throws Exception {
        mvc.perform(
                        get("/bookings/1")
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetAllWithoutUserId() throws Exception {
        mvc.perform(
                        get("/bookings")
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetAllByOwnerWithoutUserId() throws Exception {
        mvc.perform(
                        get("/bookings/owner")
                )
                .andExpect(status().isInternalServerError());
    }
}
