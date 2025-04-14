package ru.otus.hw.utils.sql;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.AppConfig;
import ru.otus.hw.utils.sql.contracts.CommandNormalizer;

import java.util.Map;
import java.util.Objects;

import static ru.otus.hw.config.Constants.NORMALIZER_SCHEME_KEY;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SqlNormalizer implements CommandNormalizer {

    AppConfig appConfig;

    @Override
    public String normalize(String command) {
        Objects.requireNonNull(this.appConfig, "Application configuration has not defined");
        var schemaName = this.appConfig.getSchemaName();
        Objects.requireNonNull(schemaName, "Target DB schema has not defined in configuration");
        var templateProcessor = new StringSubstitutor(Map.of(NORMALIZER_SCHEME_KEY, schemaName));
        return templateProcessor.replace(command);
    }
}
