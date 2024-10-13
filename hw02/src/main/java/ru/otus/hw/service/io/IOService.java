package ru.otus.hw.service.io;

public interface IOService {
    void print(String s);

    void printLine(String s);

    void printFormattedLine(String s, Object ...args);

    void printError(String err);

    void printEmptyLine();

    String readString();

    String readStringWithPrompt(String prompt);

    int readIntForRange(int min, int max, String errorMessage);

    int readIntForRangeWithPrompt(int min, int max, String prompt, String errorMessage);
}
