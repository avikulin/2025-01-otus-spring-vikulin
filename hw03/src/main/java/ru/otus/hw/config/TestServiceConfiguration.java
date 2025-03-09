package ru.otus.hw.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.otus.hw.config.contracts.TestConfiguration;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "test")
public class TestServiceConfiguration implements TestConfiguration {

    int rightAnswersCountToPass;

    int maxNumberOfInputDataAttempts;
}
