package ru.otus.hw.exceptions;

public class IncorrectAnswerException extends RuntimeException {
    public IncorrectAnswerException(String message) {
        super(message);
    }

    public IncorrectAnswerException(String message, Throwable cause) {
        super(message, cause);
    }
}
