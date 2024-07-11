package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItems;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom er = new EasyRandom();

    @Test
    public void testCreateWithoutUserId() throws Exception {
        mvc.perform(
                        post("/requests")
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetRequestsOwnerWithoutUserId() throws Exception {
        mvc.perform(
                        get("/requests")
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCreateFailedDescription() throws Exception {
        ItemRequestDto request = new ItemRequestDto(null);

        mvc.perform(
                        post("/requests")
                                .content(mapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        request.setDescription("");

        mvc.perform(
                        post("/requests")
                                .content(mapper.writeValueAsString(request))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetRequestsWithoutPageParameters() throws Exception {
        ItemRequestWithItems ir = er.nextObject(ItemRequestWithItems.class);

        when(requestService.getRequests(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(ir));

        mvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetRequestsWithoutUserId() throws Exception {
        mvc.perform(
                        get("/requests/all")
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetRequestByIdWithoutUserId() throws Exception {
        mvc.perform(
                        get("/requests/1")
                )
                .andExpect(status().isInternalServerError());
    }
}
