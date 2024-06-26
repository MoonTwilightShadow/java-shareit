package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.EmptyEmailExeption;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final UserStorage userStorage;

    @Override
    public User getById(Integer id) {
        log.info("getById user method");

        if (userStorage.getById(id) == null)
            throw new NotFoundException();

        return userStorage.getById(id);
    }

    @Override
    public List<User> getAll() {
        log.info("getAll user method");
        return userStorage.getAll();
    }

    @Override
    public User create(UserRequest request) {
        log.info("create user method");

        User user = modelMapper.map(request, User.class);

        if (user.getEmail() == null) {
            throw new EmptyEmailExeption("Email не может быть пустым");
        }

        return userStorage.create(user);
    }

    @Override
    public User update(UserRequest request, Integer id) {
        log.info("update user method");

        User user = modelMapper.map(request, User.class);
        user.setId(id);

        return userStorage.update(user);
    }

    @Override
    public void delete(Integer id) {
        log.info("delete user method");

        userStorage.delete(id);
    }
}
