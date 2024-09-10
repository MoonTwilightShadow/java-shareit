package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemContollerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom er = new EasyRandom();

    private CreateItemRequest itemRequest;
    private ItemResponse itemResponse;

    @BeforeEach
    void setUp() {
        itemRequest = new CreateItemRequest(
                "Item",
                "Description",
                true,
                null
        );

        itemResponse = new ItemResponse(
                1,
                "Item",
                "Description",
                true,
                1,
                null
        );
    }

    @Test
    public void testCreateWithoutUserId() throws Exception {
        mvc.perform(
                        post("/items")
                                .content(mapper.writeValueAsString(itemRequest))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCreateFailedName() throws Exception {
        itemRequest.setName("");

        mvc.perform(
                        post("/items")
                                .content(mapper.writeValueAsString(itemRequest))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateFailedDescription() throws Exception {
        itemRequest.setDescription("");

        mvc.perform(
                        post("/items")
                                .content(mapper.writeValueAsString(itemRequest))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateFailedAvailable() throws Exception {
        itemRequest.setAvailable(null);

        mvc.perform(
                        post("/items")
                                .content(mapper.writeValueAsString(itemRequest))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreate() throws Exception {
        when(itemService.create(any(), anyInt()))
                .thenReturn(itemResponse);

        mvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", "1")
                                .content(mapper.writeValueAsString(itemRequest))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemResponse.getOwnerId()), Integer.class));
    }

    @Test
    public void testGetByIdWithoutUserId() throws Exception {
        mvc.perform(
                        get("/items/1")
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetByOwnerWithoutPageParameters() throws Exception {
        ItemWithBookingResponse item = er.nextObject(ItemWithBookingResponse.class);

        when(itemService.getByOwner(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetByOwner() throws Exception {
        ItemWithBookingResponse item = er.nextObject(ItemWithBookingResponse.class);

        when(itemService.getByOwner(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(
                        get("/items?from=0&size=10")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(item.getName())))
                .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(item.getAvailable())))
                .andExpect(jsonPath("$.[0].ownerId", is(item.getOwnerId()), Integer.class))
                .andExpect(jsonPath("$.[0].comments.size()", is(item.getComments().size())));
    }

    @Test
    public void testSearchWithoutText() throws Exception {
        mvc.perform(
                        get("/items/search")
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testSearchWithoutPageParameters() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemResponse));

        mvc.perform(
                        get("/items/search?text=" + itemResponse.getName())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testSearch() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemResponse));

        mvc.perform(
                        get("/items/search?text=" + itemResponse.getName())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemResponse.getAvailable())))
                .andExpect(jsonPath("$.[0].ownerId", is(itemResponse.getOwnerId()), Integer.class));
    }

    @Test
    public void testUpdateWithoutUserId() throws Exception {
        mvc.perform(
                        patch("/items/1")
                                .content(mapper.writeValueAsString(itemRequest))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testUpdate() throws Exception {
        when(itemService.update(any(), anyInt(), anyInt()))
                .thenReturn(itemResponse);

        mvc.perform(
                        patch("/items/1")
                                .header("X-Sharer-User-Id", "1")
                                .content(mapper.writeValueAsString(itemRequest))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemResponse.getOwnerId()), Integer.class));
    }

    @Test
    public void testDelete() throws Exception {
        mvc.perform(
                        delete("/items/1")
                )
                .andExpect(status().isOk());
    }



    @Test
    public void addCommentWithoutUserId() throws Exception {
        CommentRequest comment = er.nextObject(CommentRequest.class);

        mvc.perform(
                        post("/items/1/comment")

                                .content(mapper.writeValueAsString(comment))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError());
    }


    @Test
    public void addComment() throws Exception {
        CommentResponse comment = er.nextObject(CommentResponse.class);
        CommentRequest cr = new CommentRequest(comment.getText());

        when(itemService.comment(anyInt(), anyInt(), any()))
                .thenReturn(comment);

        mvc.perform(
                        post("/items/1/comment")
                                .header("X-Sharer-User-Id", "1")
                                .content(mapper.writeValueAsString(cr))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())));
    }


}
