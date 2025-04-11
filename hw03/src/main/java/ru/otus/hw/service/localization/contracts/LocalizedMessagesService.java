package ru.otus.hw.service.localization.contracts;

public interface LocalizedMessagesService {
    String getMessage(String code, Object ...args);
}
