package ru.otus.hw.service.io.contracts;

import java.util.List;

public interface IOService {
    void print(String s);
    void printLine(String s);
    void printFormattedLine(String s, Object ...args);
    void printError(String err);
    void printEmptyLine();
    String readString();
    String readStringWithPrompt(String prompt);

    // слегка перепилил интерфейс, чтобы были возможны множественные ответы (по условию задачи)
    List<Integer> readIntForRange(int min, int max, String errorMessage);
    List<Integer> readIntForRangeWithPrompt(int min, int max, String prompt, String errorMessage);
}
