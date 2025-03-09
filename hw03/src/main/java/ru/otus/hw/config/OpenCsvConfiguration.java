package ru.otus.hw.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.otus.hw.config.contracts.TestFileReaderConfiguration;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "opencsv.settings")
public class OpenCsvConfiguration implements TestFileReaderConfiguration {
    String testFileName;

    char columnSeparationSymbol;

    int numberOfRowsSkipped;
}
