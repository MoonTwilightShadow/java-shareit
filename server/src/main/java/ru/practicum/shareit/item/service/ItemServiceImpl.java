package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.NotOwnerException;
import ru.practicum.shareit.exception.exceptions.OwnerExeption;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemWithBookingResponse getById(Integer id, Integer userId) {
        log.info("getById item method");

        ItemWithBookingResponse item = ItemMapper.mapToBookingResponse(itemRepository.findById(id).orElseThrow(NotFoundException::new));
        LocalDateTime current = LocalDateTime.now();

        if (item.getOwnerId().equals(userId)) {
            Optional<Booking> last = bookingRepository.findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(current, Status.APPROVED, id);
            Optional<Booking> next = bookingRepository.findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(current, Status.APPROVED, id);

            last.ifPresent(booking -> item.setLastBooking(BookingMapper.mapToShortResponse(booking)));
            next.ifPresent(booking -> item.setNextBooking(BookingMapper.mapToShortResponse(booking)));
        }

        item.setComments(commentRepository.findCommentsByItemId(id).stream()
                .map(ItemMapper::mapToCommentResponse)
                .collect(Collectors.toList()));

        return item;
    }

    @Override
    public List<ItemWithBookingResponse> getByOwner(Integer owner, Integer from, Integer size) {
        log.info("getByOwner item method");

        if (userRepository.findById(owner).isEmpty()) {
            throw new NotOwnerException();
        }

        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException();
        }

        PageRequest page = PageRequest.of(from / size, size);
        List<ItemWithBookingResponse> items = itemRepository.findItemsByOwnerIdOrderByIdAsc(owner, page).stream()
                .map(ItemMapper::mapToBookingResponse)
                .collect(Collectors.toList());

        LocalDateTime current = LocalDateTime.now();

        for (ItemWithBookingResponse item : items) {
            Optional<Booking> last = bookingRepository.findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(current, Status.APPROVED, item.getId());
            Optional<Booking> next = bookingRepository.findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(current, Status.APPROVED, item.getId());

            last.ifPresent(booking -> item.setLastBooking(BookingMapper.mapToShortResponse(booking)));
            next.ifPresent(booking -> item.setNextBooking(BookingMapper.mapToShortResponse(booking)));

            item.setComments(commentRepository.findCommentsByItemId(item.getId()).stream()
                    .map(ItemMapper::mapToCommentResponse)
                    .collect(Collectors.toList()));
        }

        return items;
    }

    @Override
    public List<ItemResponse> search(String text, Integer from, Integer size) {
        log.info("search item method");

        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException();
        }

        PageRequest page = PageRequest.of(from / size, size);
        return itemRepository.findItemsByNameOrDescriptionContainsIgnoreCaseAndAvailableTrue(text, text, page).stream()
                .map(ItemMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponse create(CreateItemRequest request, Integer ownerId) {
        log.info("create item method");

        Item item = ItemMapper.mapFromRequest(request);

        User user = userRepository.findById(ownerId).orElseThrow(NotOwnerException::new);
        item.setOwner(user);

        Integer requestId = request.getRequestId();
        if (requestId != null) {
            Optional<Request> itemRequest = requestRepository.findById(requestId);

            if (itemRequest.isEmpty()) {
                throw new NotFoundException();
            }

            item.setRequest(itemRequest.get());
        } else {
            item.setRequest(null);
        }
        return ItemMapper.mapToResponse(itemRepository.save(item));
    }


    @Override
    public ItemResponse update(UpdateItemRequest request, Integer itemId, Integer ownerId) {
        log.info("update item method");

        Item item = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);

        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new OwnerExeption();
        }

        if (request.getName() != null) {
            item.setName(request.getName());
        }

        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }

        if (request.getAvailable() != null) {
            item.setAvailable(request.getAvailable());
        }

        return ItemMapper.mapToResponse(itemRepository.save(item));
    }

    @Override
    public void delete(Integer id) {
        log.info("delete item method");

        itemRepository.deleteById(id);
    }

    @Override
    public CommentResponse comment(Integer itemId, Integer userId, CommentRequest comment) {
        Item item = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);

        if (bookingRepository.findCompletedBooking(itemId, userId).isEmpty() || comment.getText().isBlank()) {
            throw new IllegalArgumentException();
        }

        Comment newComment = new Comment();
        newComment.setItem(item);
        newComment.setAuthor(user);
        newComment.setText(comment.getText());
        newComment.setCreated(LocalDateTime.now());
        return ItemMapper.mapToCommentResponse(commentRepository.save(newComment));
    }
}
