package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserStorageImpl implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private Set<String> uniqEmail = new HashSet<>();
    private int nextId = 1;

    @Override
    public User getById(Integer id) {
        return users.getOrDefault(id, null);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        if (uniqEmail.contains(user.getEmail())) {
            throw new EmailAlreadyExistException("Такой email уже зарегистрирован");
        }

        uniqEmail.add(user.getEmail());

        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (uniqEmail.contains(user.getEmail())) {
            throw new EmailAlreadyExistException("Такой email уже зарегистрирован");
        }

        User saveUser = users.get(user.getId());

        if (user.getEmail() != null) {
            uniqEmail.remove(saveUser.getEmail());
            saveUser.setEmail(user.getEmail());
        }

        if (user.getName() != null)
            saveUser.setName(user.getName());

        users.put(saveUser.getId(), saveUser);
        return users.get(user.getId());
    }

    @Override
    public void delete(Integer id) {
        uniqEmail.remove(users.get(id).getEmail());
        users.remove(id);
    }
}
