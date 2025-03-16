package ru.otus.hw.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.contracts.TestPropertiesProvider;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "test")
public class TestServicePropertiesProvider implements TestPropertiesProvider {

    int rightAnswersCountToPass;

    int maxNumberOfInputDataAttempts;
}
