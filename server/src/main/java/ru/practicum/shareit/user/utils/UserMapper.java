package ru.practicum.shareit.user.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    public User mapFromRequest(UserRequest request) {
        return new User(
                request.getName(),
                request.getEmail()
        );
    }

    public UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
