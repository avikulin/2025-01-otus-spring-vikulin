package ru.otus.hw.service.ioservice.utils;

public class Normalizer {
    private static final String NEW_LINE_PATTERN = "\\\\n";

    public static String newLine(String input){
        return input.replaceAll(NEW_LINE_PATTERN, System.lineSeparator());
    }
}
