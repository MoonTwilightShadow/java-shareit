package ru.practicum.shareit.exception.exceptions;

public class EmptyEmailExeption extends RuntimeException {
    private String message;

    public EmptyEmailExeption(String message) {
        super(message);
    }
}
