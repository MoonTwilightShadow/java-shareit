package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String userIdHeader = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ItemWithBookingResponse getById(@PathVariable Integer itemId, @RequestHeader(userIdHeader) Integer userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingResponse> getByOwner(@RequestHeader(userIdHeader) Integer owner) {
        return itemService.getByOwner(owner);
    }

    @GetMapping("/search")
    public List<ItemResponse> search(@RequestParam("text") String text) {
        return itemService.search(text);
    }

    @PostMapping
    public Item create(@Valid @RequestBody CreateItemRequest request, @RequestHeader(userIdHeader) Integer ownerId) {
        return itemService.create(request, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@RequestBody UpdateItemRequest request, @PathVariable Integer itemId, @RequestHeader(userIdHeader) Integer ownerId) {
        return itemService.update(request, itemId, ownerId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Integer itemId) {
        itemService.delete(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse comment(@PathVariable Integer itemId, @RequestHeader(userIdHeader) Integer userId, @RequestBody CommentRequest comment) {
        return itemService.comment(itemId, userId, comment);
    }
}
