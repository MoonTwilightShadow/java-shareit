package ru.practicum.shareit.exception.exceptions;

public class EmailAlreadyExistException extends RuntimeException {
    private String message;

    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
