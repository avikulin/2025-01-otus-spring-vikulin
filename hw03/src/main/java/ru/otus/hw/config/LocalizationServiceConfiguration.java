package ru.otus.hw.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.contracts.LocaleConfig;

import java.util.Locale;

@Data
@ConfigurationProperties(prefix = "test")
public class LocalizationServiceConfiguration implements LocaleConfig {
    Locale locale;
}
