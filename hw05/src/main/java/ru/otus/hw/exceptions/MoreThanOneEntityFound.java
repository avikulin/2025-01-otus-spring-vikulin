package ru.otus.hw.exceptions;

public class MoreThanOneEntityFound extends RuntimeException {
    public MoreThanOneEntityFound(String message) {
        super(message);
    }
}
