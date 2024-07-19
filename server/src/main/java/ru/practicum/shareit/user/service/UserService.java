package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User getById(Integer id);

    List<User> getAll();

    User create(UserRequest request);

    User update(UserRequest request, Integer id);

    void delete(Integer id);
}
