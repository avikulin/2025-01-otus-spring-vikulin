package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.command.annotation.CommandScan;
import ru.otus.hw.config.OpenCsvConfiguration;
import ru.otus.hw.config.TestServiceConfiguration;

@SpringBootApplication
@CommandScan
@Profile({"production","localized"})
@EnableConfigurationProperties({OpenCsvConfiguration.class, TestServiceConfiguration.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}