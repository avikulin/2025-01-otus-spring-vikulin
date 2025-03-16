package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import ru.otus.hw.config.LocalizationServiceConfiguration;
import ru.otus.hw.config.OpenCsvPropertiesProvider;
import ru.otus.hw.config.TestServicePropertiesProvider;

@SpringBootApplication
@Profile({"production","localized"})
@EnableConfigurationProperties({TestServicePropertiesProvider.class,
                                OpenCsvPropertiesProvider.class,
                                LocalizationServiceConfiguration.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}