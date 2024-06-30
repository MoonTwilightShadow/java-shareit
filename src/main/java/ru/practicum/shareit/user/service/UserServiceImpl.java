package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.exception.exceptions.EmptyEmailExeption;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public User getById(Integer id) {
        log.info("getById user method");

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new NotFoundException();
        }

        return user.get();
    }

    @Override
    public List<User> getAll() {
        log.info("getAll user method");
        return userRepository.findAll();
    }

    @Override
    public User create(UserRequest request) {
        log.info("create user method");

        User user = modelMapper.map(request, User.class);

        if (user.getEmail() == null) {
            throw new EmptyEmailExeption("Email не может быть пустым");
        }

        return userRepository.save(user);
    }

    @Override
    public User update(UserRequest request, Integer id) {
        log.info("update user method");

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new NotFoundException();
        }

        if (request.getEmail() != null) {
                user.get().setEmail(request.getEmail());
        }

        if (request.getName() != null) {
            user.get().setName(request.getName());
        }
        return userRepository.save(user.get());
    }

    @Override
    public void delete(Integer id) {
        log.info("delete user method");

        userRepository.deleteById(id);
    }
}
