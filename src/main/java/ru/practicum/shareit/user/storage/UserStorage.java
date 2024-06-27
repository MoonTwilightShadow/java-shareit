package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User getById(Integer id);

    List<User> getAll();

    User create(User user);

    User update(User user);

    void delete(Integer id);
}
