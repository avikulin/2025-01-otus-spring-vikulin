package ru.otus.hw.exceptions;

import jakarta.persistence.NoResultException;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, NoResultException e) {
        super(message);
    }
}
