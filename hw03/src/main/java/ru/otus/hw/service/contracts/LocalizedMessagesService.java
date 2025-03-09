package ru.otus.hw.service.contracts;

public interface LocalizedMessagesService {
    String getMessage(String code, Object ...args);
}
