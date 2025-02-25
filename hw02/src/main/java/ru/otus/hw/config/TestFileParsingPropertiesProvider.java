package ru.otus.hw.config;

import org.springframework.stereotype.Component;

@Component
public interface TestFileParsingPropertiesProvider {
    char getColumnSeparationSymbol();

    int getNumberOfRowsSkipped();
}
