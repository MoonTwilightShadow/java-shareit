package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithItems;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private RequestServiceImpl requestService;

    private User requestor;
    private Request request;

    @BeforeEach
    void setUp() {
        requestor = new User(
                1,
                "User",
                "mail@mail.ru"

        );

        request = new Request(
                1,
                "Request",
                requestor,
                LocalDateTime.now()
        );
    }

    @Test
    public void testCreateUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        ItemRequestDto reqDto = new ItemRequestDto("Request");

        assertThrows(NotFoundException.class,
                () -> requestService.create(reqDto, requestor.getId()));

        verify(requestRepository, never()).save(any());
    }

    @Test
    public void testCreate() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(requestRepository.save(any())).thenReturn(request);

        ItemRequestDto reqDto = new ItemRequestDto("Request");
        ItemRequestResponse resp = requestService.create(reqDto, requestor.getId());

        assertThat(resp.getId(), notNullValue());
    }

    @Test
    public void testGetRequestOwnerUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.getRequestsOwner(requestor.getId()));

        verify(requestRepository, never()).findRequestByRequestorOrderByCreatedDesc(any());
        verify(itemRepository, never()).findItemsByRequestId(anyInt());
    }

    @Test
    public void testGetRequestOwnerEmptyItems() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(requestRepository.findRequestByRequestorOrderByCreatedDesc(any())).thenReturn(List.of(request));
        when(itemRepository.findItemsByRequestId(anyInt())).thenReturn(List.of());

        List<ItemRequestWithItems> requests = requestService.getRequestsOwner(requestor.getId());

        assertEquals(1, requests.size());
        assertEquals(0, requests.get(0).getItems().size());
    }

    @Test
    public void testGetRequestNotFoundUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.getRequests(0, 10, 10));

        verify(requestRepository, never()).findRequestByRequestorNotOrderByCreatedDesc(any(), any());
        verify(itemRepository, never()).findItemsByRequestId(anyInt());
    }

    @Test
    public void testGetRequestsFromSizeFailed() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));

        assertThrows(IllegalArgumentException.class,
                () -> requestService.getRequests(-1, 10, requestor.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> requestService.getRequests(-1, 0, requestor.getId()));
        assertThrows(IllegalArgumentException.class,
                () -> requestService.getRequests(-1, -10, requestor.getId()));

        verify(requestRepository, never()).findRequestByRequestorNotOrderByCreatedDesc(any(), any());
        verify(itemRepository, never()).findItemsByRequestId(anyInt());
    }

    @Test
    public void testGetRequests() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(requestRepository.findRequestByRequestorNotOrderByCreatedDesc(any(), any())).thenReturn(new PageImpl<>(List.of(request)));
        when(itemRepository.findItemsByRequestId(anyInt())).thenReturn(List.of());

        List<ItemRequestWithItems> requests = requestService.getRequests(0, 10, requestor.getId());

        assertEquals(1, requests.size());
        assertEquals(0, requests.get(0).getItems().size());
    }

    @Test
    public void testGetRequestByIdUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(request.getId(), requestor.getId()));

        verify(requestRepository, never()).findById(any());
        verify(itemRepository, never()).findItemsByRequestId(anyInt());
    }

    @Test
    public void testGetRequestByIdRequestNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(requestRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(request.getId(), requestor.getId()));

        verify(itemRepository, never()).findItemsByRequestId(anyInt());
    }

    @Test
    public void testGetRequestById() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(requestRepository.findById(anyInt())).thenReturn(Optional.of(request));
        when(itemRepository.findItemsByRequestId(anyInt())).thenReturn(List.of());

        ItemRequestWithItems req = requestService.getRequestById(request.getId(), requestor.getId());

        assertThat(req, notNullValue());
        assertThat(req.getItems().size(), equalTo(0));
    }
}
