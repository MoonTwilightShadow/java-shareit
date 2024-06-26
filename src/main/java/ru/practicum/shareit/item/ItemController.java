package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
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
    private final String userIdHeader = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public Item getById(@PathVariable Integer itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public List<Item> getByOwner(@RequestHeader(userIdHeader) Integer owner) {
        return itemService.getByOwner(owner);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam("text") String text) {
        return itemService.search(text);
    }

    @PostMapping
    public Item create(@Valid @RequestBody CreateItemRequest request, @RequestHeader(userIdHeader) Integer owner) {
        request.setOwner(owner);
        return itemService.create(request);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestBody UpdateItemRequest request, @PathVariable Integer itemId, @RequestHeader(userIdHeader) Integer owner) {
        request.setId(itemId);
        request.setOwner(owner);
        return itemService.update(request);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Integer itemId) {
        itemService.delete(itemId);
    }
}
