package ru.otus.hw.exceptions;

public class EntityValidationException extends IllegalArgumentException {
    public EntityValidationException(String message) {
        super(message);
    }
}
