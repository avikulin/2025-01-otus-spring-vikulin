package ru.otus.hw.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.contracts.TestConfig;
import ru.otus.hw.config.contracts.TestFileReaderConfig;

@Data
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppProperties implements TestConfig, TestFileReaderConfig {
    @Value("${test.filename}")
    String testFileName;

    @Value("${opencsv.settings.column-separation-symbol}")
    char columnSeparationSymbol;

    @Value("${opencsv.settings.skip-first-rows}")
    int numberOfRowsSkipped;

    @Value("${test.right-answers-count-to-pass}")
    int rightAnswersCountToPass;

    @Value("${test.max-number-of-input-data-attempts}")
    int maxNumberOfInputDataAttempts;
}
