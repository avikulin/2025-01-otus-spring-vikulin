package ru.otus.hw.utils.factories.exceptions.contracts;

public interface LoggedExceptionFactory {
    void logAndThrow(String text, Class<? extends RuntimeException> exception);

    void logAndThrow(String text, Class<? extends RuntimeException> exception, Throwable cause);

    void logAndThrow(String text, Long id, Class<? extends RuntimeException> exception);

    void logAndThrow(String text, Long id, Class<? extends RuntimeException> exception, Throwable cause);
}
