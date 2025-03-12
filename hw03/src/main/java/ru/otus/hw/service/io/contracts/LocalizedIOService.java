package ru.otus.hw.service.io.contracts;

import java.util.List;

public interface LocalizedIOService extends IOService {
    void printErrorLocalized(String err);

    void printLineLocalized(String code);

    void printFormattedLineLocalized(String code, Object ...args);

    String readStringWithPromptLocalized(String promptCode);

    List<Integer> readIntForRangeLocalized(int min, int max, String errorMessageCode);

    List<Integer> readIntForRangeWithPromptLocalized(int min, int max, String promptCode, String errorMessageCode);
}
