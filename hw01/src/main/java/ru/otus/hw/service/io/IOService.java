package ru.otus.hw.service.io;

public interface IOService {
    void printLine(String s);

    void printFormattedLine(String s, Object ...args);

    void printEmptyLine();
}
