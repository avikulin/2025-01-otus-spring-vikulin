package ru.otus.hw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.contracts.TestConfig;
import ru.otus.hw.config.contracts.TestFileReaderConfig;

@Data
@Component
public class AppProperties implements TestConfig, TestFileReaderConfig {
    @Value("${test.filename}")
    private String testFileName;

    @Value("${opencsv.settings.column-separation-symbol}")
    private char columnSeparationSymbol;

    @Value("${opencsv.settings.skip-first-rows}")
    private int numberOfRowsSkipped;

    @Value("${test.right-answers-count-to-pass}")
    private int rightAnswersCountToPass;

    @Value("${test.max-number-of-input-data-attempts}")
    int maxNumberOfInputDataAttempts;
}
