package ru.otus.hw.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.otus.hw.config.contracts.TestFileParsingPropertiesProvider;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "opencsv")
public class OpenCsvConfiguration implements TestFileParsingPropertiesProvider {
    char columnSeparationSymbol;

    int numberOfRowsSkipped;
}
