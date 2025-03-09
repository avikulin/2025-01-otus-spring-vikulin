package ru.otus.hw.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.otus.hw.config.contracts.LocaleConfig;

import java.util.Locale;

@Data
@ConfigurationProperties(prefix = "test")
public class LocalizationServiceConfiguration implements LocaleConfig {
    Locale locale;
}
