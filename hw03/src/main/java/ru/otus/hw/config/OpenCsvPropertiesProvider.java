package ru.otus.hw.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.contracts.TestFileReaderPropertiesProvider;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "opencsv.settings")
public class OpenCsvPropertiesProvider implements TestFileReaderPropertiesProvider {
    String testFileName;

    char columnSeparationSymbol;

    int numberOfRowsSkipped;
}
