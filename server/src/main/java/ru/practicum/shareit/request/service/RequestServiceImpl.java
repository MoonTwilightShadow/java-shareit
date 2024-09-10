package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithItems;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.utils.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestResponse create(ItemRequestDto requestDto, Integer requestorId) {
        log.info("create method requests");

        Request request = ItemRequestMapper.mapFromRequest(requestDto);
        request.setRequestor(userRepository.findById(requestorId).orElseThrow(NotFoundException::new));

        return ItemRequestMapper.mapToResponse(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestWithItems> getRequestsOwner(Integer requestorId) {
        log.info("getRequestsOwner method requests");

        User requestor = userRepository.findById(requestorId).orElseThrow(NotFoundException::new);

        return requestRepository.findRequestByRequestorOrderByCreatedDesc(requestor).stream()
                .map(request -> ItemRequestMapper.mapToResponseWithItems(request, itemRepository.findItemsByRequestId(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestWithItems> getRequests(Integer from, Integer size, Integer userId) {
        log.info("getRequests method requests");

        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);

        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException();
        }

        PageRequest page = PageRequest.of(from / size, size);
        List<Request> requests = requestRepository.findRequestByRequestorNotOrderByCreatedDesc(user, page).getContent();

        return requests.stream()
                .map(request -> ItemRequestMapper.mapToResponseWithItems(request, itemRepository.findItemsByRequestId(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestWithItems getRequestById(Integer requestId, Integer userId) {
        log.info("getRequestsById method requests");

        userRepository.findById(userId).orElseThrow(NotFoundException::new);

        return ItemRequestMapper.mapToResponseWithItems(requestRepository.findById(requestId).orElseThrow(NotFoundException::new),
                itemRepository.findItemsByRequestId(requestId));
    }
}
