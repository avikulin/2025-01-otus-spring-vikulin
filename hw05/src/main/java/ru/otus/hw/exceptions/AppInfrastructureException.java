package ru.otus.hw.exceptions;

public class AppInfrastructureException extends RuntimeException {
    public AppInfrastructureException(String message) {
        super(message);
    }

    public AppInfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}
