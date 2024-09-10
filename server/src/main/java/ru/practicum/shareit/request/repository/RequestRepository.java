package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findRequestByRequestorOrderByCreatedDesc(User requestor);

    Page<Request> findRequestByRequestorNotOrderByCreatedDesc(User requestor, Pageable page);
}