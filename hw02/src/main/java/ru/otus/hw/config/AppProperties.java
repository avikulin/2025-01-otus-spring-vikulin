package ru.otus.hw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component("appProperties")
@PropertySource("classpath:application.properties")
public class AppProperties implements TestConfig, TestFileReaderConfig {
    @Value("${test.fileName}")
    private String testFileName;

    @Value("${opencsv.settings.column-separation-symbol}")
    private char columnSeparationSymbol;

    @Value("${opencsv.settings.skip-first-rows}")
    private int numberOfRowsSkipped;

    @Value("${test.rightAnswersCountToPass}")
    private int rightAnswersCountToPass;
}
