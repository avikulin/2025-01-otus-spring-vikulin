package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.otus.hw.config.LocalizationServiceConfiguration;
import ru.otus.hw.config.OpenCsvConfiguration;
import ru.otus.hw.config.TestServiceConfiguration;
import ru.otus.hw.service.contracts.TestRunnerService;

@SpringBootApplication
@EnableConfigurationProperties({TestServiceConfiguration.class,
                                OpenCsvConfiguration.class,
                                LocalizationServiceConfiguration.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}