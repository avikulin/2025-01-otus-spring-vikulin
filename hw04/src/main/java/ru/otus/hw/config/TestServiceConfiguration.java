package ru.otus.hw.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.otus.hw.config.contracts.LocaleConfig;
import ru.otus.hw.config.contracts.TestConfig;
import ru.otus.hw.config.contracts.TestFileNameProvider;

import java.util.Locale;
import java.util.Map;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "test")
public class TestServiceConfiguration implements TestConfig, TestFileNameProvider, LocaleConfig {
    @Getter
    @Setter
    int rightAnswersCountToPass;

    @Getter
    @Setter
    int maxNumberOfInputDataAttempts;

    @Getter
    private Locale locale;

    @Setter
    private Map<String, String> fileNameByLocaleTag;

    public void setLocale(String locale) {
        this.locale = Locale.forLanguageTag(locale);
    }

    @Override
    public String getTestFileName() {
        log.info("Trying to get test file name for locale: {}", locale);
        return fileNameByLocaleTag.get(locale.toLanguageTag());
    }
}
