package ru.otus.hw.utils.formatters.localized.input;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.service.ioservice.config.LocalizedIoStubsConfig;
import ru.otus.hw.utils.formatters.base.DefaultInputStreamFormatter;
import ru.otus.hw.utils.formatters.localized.contracts.LocalizedInputFormatter;
import ru.otus.hw.utils.formatters.providers.InputStreamFormatterNegativeArgsProvider;
import ru.otus.hw.utils.formatters.providers.InputStreamFormatterPositiveArgsProvider;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {LocalizedIoStubsConfig.class})
@DisplayName("Input data formatting behaviour check")
@TestPropertySource(properties = "test.locale=ru-RU")
@Import(DefaultInputStreamFormatter.class)
@ActiveProfiles("localized")
class LocalizedRuRuInputStreamFormatterTest extends ConfigurableByPropertiesTestBase {
    @Autowired
    LocalizedInputFormatter formatter;

    @DisplayName("Positive tests")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(InputStreamFormatterPositiveArgsProvider.class)
    void runPositiveTests(String testName, String input, List<Integer> expected) {
        assertEquals(expected, this.formatter.parseAnswers(input));
    }

    @DisplayName("Negative tests")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(InputStreamFormatterNegativeArgsProvider.class)
    void runNegativeTests(String testName, String input) {
        assertThrows(IncorrectAnswerException.class, ()->{
            var r = formatter.parseAnswers(input)
                             .stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining(", "));
            System.out.println("["+input+"] = "+r);
        });
    }
}