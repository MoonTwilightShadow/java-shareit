package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            Optional<Booking> last = bookingRepository.findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(current, Status.APPROVED, item.getId());
            Optional<Booking> next = bookingRepository.findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(current, Status.APPROVED, item.getId());

            last.ifPresent(booking -> item.setLastBooking(BookingMapper.mapToShortResponse(booking)));
            next.ifPresent(booking -> item.setNextBooking(BookingMapper.mapToShortResponse(booking)));
        }

        return item;
    }

    @Override
    public List<ItemWithBookingResponse> getByOwner(Integer owner) {
        log.info("getByOwner item method");

        if (userRepository.findById(owner).isEmpty()) {
            throw new NotOwnerException();
        }

        List<ItemWithBookingResponse> items = itemRepository.findItemsByOwnerIdOrderByIdAsc(owner).stream()
                .map(ItemMapper::mapToBookingResponse)
                .toList();

        LocalDateTime current = LocalDateTime.now();

        for (ItemWithBookingResponse item : items) {
            Optional<Booking> last = bookingRepository.findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(current, Status.APPROVED, item.getId());
            Optional<Booking> next = bookingRepository.findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(current, Status.APPROVED, item.getId());

            last.ifPresent(booking -> item.setLastBooking(BookingMapper.mapToShortResponse(booking)));
            next.ifPresent(booking -> item.setNextBooking(BookingMapper.mapToShortResponse(booking)));
        }

        return items;
    }

    @Override
    public List<ItemResponse> search(String text) {
        log.info("search item method");

        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.findItemsByNameOrDescriptionContainsIgnoreCaseAndAvailableTrue(text, text).stream()
                .map(ItemMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Item create(CreateItemRequest request, Integer ownerId) {
        log.info("create item method");

        Item item = ItemMapper.mapFromRequest(request);

        Optional<User> user = userRepository.findById(ownerId);
        if (user.isEmpty()) {
            throw new NotOwnerException();
        }
        item.setOwner(user.get());

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
        return itemRepository.save(item);
    }


    @Override
    public ItemResponse update(UpdateItemRequest request, Integer itemId, Integer ownerId) {
        log.info("update item method");

        Optional<Item> item = itemRepository.findById(itemId);

        if (item.isEmpty()) {
            throw new NotFoundException();
        }

        if (!Objects.equals(item.get().getOwner().getId(), ownerId)) {
            throw new OwnerExeption();
        }

        if (request.getName() != null) {
            item.get().setName(request.getName());
        }

        if (request.getDescription() != null) {
            item.get().setDescription(request.getDescription());
        }

        if (request.getAvailable() != null) {
            item.get().setAvailable(request.getAvailable());
        }

        return ItemMapper.mapToResponse(itemRepository.save(item.get()));
    }

    @Override
    public void delete(Integer id) {
        log.info("delete item method");

        itemRepository.deleteById(id);
    }

    @Override
    public CommentResponse comment(Integer itemId, Integer userId, String text) {
        Item item = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);

        if (bookingRepository.findBookingByItemAndBooker(item, user).isEmpty()) {
            throw new IllegalArgumentException();
        }

        return ItemMapper.mapToCommentResponse(commentRepository.save(new Comment(text, item, user)));
    }
}
