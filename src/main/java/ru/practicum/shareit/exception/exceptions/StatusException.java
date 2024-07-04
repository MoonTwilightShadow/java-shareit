package ru.practicum.shareit.exception.exceptions;

public class StatusException extends RuntimeException {
    public StatusException(String state) {
        super("Unknown state: " + state);
    }
}
