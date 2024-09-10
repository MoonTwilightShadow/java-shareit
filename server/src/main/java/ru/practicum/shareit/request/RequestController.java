package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithItems;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestResponse create(@Valid @RequestBody ItemRequestDto request, @RequestHeader(USER_ID_HEADER) Integer requestorId) {
        return requestService.create(request, requestorId);
    }

    @GetMapping
    public List<ItemRequestWithItems> getRequestsOwner(@RequestHeader(USER_ID_HEADER) Integer requestorId) {
        return requestService.getRequestsOwner(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItems> getRequests(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                  @RequestHeader(USER_ID_HEADER) Integer userId) {
        return requestService.getRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItems getRequestById(@PathVariable Integer requestId,
                                               @RequestHeader(USER_ID_HEADER) Integer userId) {
        return requestService.getRequestById(requestId, userId);
    }

}
