package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(USER_ID_HEADER) Integer userId,
            @PathVariable Integer itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwner(
            @RequestHeader(USER_ID_HEADER) Integer userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get items with userId={}, from={}, size={}", userId, from, size);
        return itemClient.getByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam("text") String text,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Search items with text={}, userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_ID_HEADER) Integer userId,
            @Validated(Marker.OnCreate.class) @RequestBody ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader(USER_ID_HEADER) Integer userId,
            @Validated(Marker.OnUpdate.class) @RequestBody ItemDto itemDto,
            @PathVariable Integer itemId) {
        log.info("Updating item with id {} on item {} user {}", itemId, itemDto, userId);
        return itemClient.update(userId, itemDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@PathVariable Integer itemId) {
        log.info("Delete item with id {}", itemId);
        return itemClient.delete(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> comment(
            @RequestHeader(USER_ID_HEADER) Integer userId,
            @PathVariable Integer itemId,
            @RequestBody CommentDto comment) {
        log.info("Creating comment {} for item={}, userId={}", comment, itemId, userId);
        return itemClient.comment(userId, itemId, comment);
    }
}
