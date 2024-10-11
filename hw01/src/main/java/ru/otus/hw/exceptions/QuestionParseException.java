package ru.otus.hw.exceptions;

public class QuestionParseException extends RuntimeException {
    public QuestionParseException(String message) {
        super(message);
    }

    public QuestionParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
