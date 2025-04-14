package ru.otus.hw.exceptions;

public class SqlCommandFailure extends RuntimeException {
    public SqlCommandFailure(String message) {
        super(message);
    }

    public SqlCommandFailure(String message, Throwable cause) {
        super(message, cause);
    }
}
