package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.exceptions.EmptyEmailExeption;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void testGetByIdNotFound() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getById(1));
        verify(userRepository).findById(anyInt());
    }

    @Test
    public void testGetByIdFound() {
        User user = new User(1, "name", "email@box.ru");

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        assertSame(user, userService.getById(1));
        verify(userRepository).findById(anyInt());
    }

    @Test
    public void testGetAllEmpty() {
        when(userRepository.findAll())
                .thenReturn(List.of());

        assertEquals(0, userService.getAll().size());
        verify(userRepository).findAll();
    }

    @Test
    public void testGetAllNotEmpty() {
        User user = new User(1, "name", "email@box.ru");
        User user2 = new User(2, "name2", "email2@box.ru");

        when(userRepository.findAll())
                .thenReturn(List.of(user, user2));

        assertEquals(2, userService.getAll().size());
        verify(userRepository).findAll();
    }

    @Test
    public void testCreateEmptyEmail() {
        UserRequest ur = new UserRequest("name", null);

        assertThrows(EmptyEmailExeption.class,
                () -> userService.create(ur));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testCreate() {
        UserRequest ur = new UserRequest("name", "email@box.ru");
        User user = new User(1, "name", "email@box.ru");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        assertSame(user, userService.create(ur));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testUpdateUserNotFound() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getById(1));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUserEmptyEmail() {
        UserRequest ur = new UserRequest("nameUpd", null);
        User user = new User(1, "name", "email@box.ru");

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        user.setName("nameUpd");
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        assertSame(user, userService.update(ur, 1));
    }

    @Test
    public void testUpdateUserEmptyName() {
        UserRequest ur = new UserRequest(null, "emailUpd@box.ru");
        User user = new User(1, "name", "email@box.ru");

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        user.setEmail("emailUpd@box.ru");
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        assertSame(user, userService.update(ur, 1));
    }

    @Test
    public void testUpdateUser() {
        UserRequest ur = new UserRequest("nameUpd", "emailUpd@box.ru");
        User user = new User(1, "name", "email@box.ru");

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        user.setName("nameUpd");
        user.setEmail("emailUpd@box.ru");
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        assertSame(user, userService.update(ur, 1));
    }
}
