package ru.otus.hw.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "catalog")
public class AppConfig {

    String schemaName;

    int inMemoryLoadThreshold;
}
