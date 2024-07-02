package ru.practicum.shareit.exception.exceptions;

import ru.practicum.shareit.booking.model.State;

public class StatusException extends RuntimeException {
    public StatusException(String state) {
        super("Unknown state: " + state);
    }
}
