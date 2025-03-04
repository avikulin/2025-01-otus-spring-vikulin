package ru.otus.hw.exceptions;

public class QuestionStateException extends RuntimeException {
    public QuestionStateException(String message) {
        super(message);
    }

    protected QuestionStateException(String message, Throwable cause) {
        super(message, cause);
    }
}