package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;


    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_ID_HEADER) Integer userId,
            @Validated(Marker.OnCreate.class) @RequestBody RequestDto requestDto) {
        log.info("Add request {} with userId={}", requestDto, userId);
        return requestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsOwner(@RequestHeader(USER_ID_HEADER) Integer userId) {
        log.info("Get user requests with userId={}", userId);
        return requestClient.getRequestsOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(
            @RequestHeader(USER_ID_HEADER) Integer userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get requests with userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(USER_ID_HEADER) Integer userId,
            @PathVariable Integer requestId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return requestClient.getById(userId, requestId);
    }

}
