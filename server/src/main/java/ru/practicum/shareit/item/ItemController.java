package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ItemWithBookingResponse getById(@PathVariable Integer itemId, @RequestHeader(USER_ID_HEADER) Integer userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingResponse> getByOwner(@RequestHeader(USER_ID_HEADER) Integer owner,
                                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemService.getByOwner(owner, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponse> search(@RequestParam("text") String text,
                                     @RequestParam(value = "from", defaultValue = "0") Integer from,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemService.search(text, from, size);
    }

    @PostMapping
    public ItemResponse create(@Valid @RequestBody CreateItemRequest request, @RequestHeader(USER_ID_HEADER) Integer ownerId) {
        return itemService.create(request, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@RequestBody UpdateItemRequest request, @PathVariable Integer itemId, @RequestHeader(USER_ID_HEADER) Integer ownerId) {
        return itemService.update(request, itemId, ownerId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Integer itemId) {
        itemService.delete(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse comment(@PathVariable Integer itemId, @RequestHeader(USER_ID_HEADER) Integer userId, @RequestBody CommentRequest comment) {
        return itemService.comment(itemId, userId, comment);
    }
}
