package ru.otus.hw.utils.formatters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.utils.formatters.base.DefaultInputStreamFormatter;
import ru.otus.hw.utils.formatters.config.FormattersContextConfiguration;
import ru.otus.hw.utils.formatters.base.contracts.InputFormatter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {FormattersContextConfiguration.class})
@DisplayName("Input data formatting behaviour check")
@Import(DefaultInputStreamFormatter.class)
@ActiveProfiles({"test","native"})
class DefaultInputStreamFormatterTest {
    @Autowired
    InputFormatter formatter;

    private static Stream<Arguments> getPositiveTestData() {
        return Stream.of(
                Arguments.of("Parse single value", "1", List.of(1)),
                Arguments.of("Parse double value with separator", "1,2", List.of(1,2)),
                Arguments.of("Parse double value with separator and space", "1, 2", List.of(1,2))
        );
    }


    private static Stream<Arguments> getNegativeTestData() {
        return Stream.of(
                Arguments.of("Throws on parse blank value", null),
                Arguments.of("Throws on parse blank value", " "),
                Arguments.of("Throws on parse non-digit value", "a1b;c]"),
                Arguments.of("Throws on parse single value with dot", "1.2"),
                Arguments.of("Throws on parse single value with extra-dots #1", "1.2."),
                Arguments.of("Throws on parse single value with extra-dots #2", "1..2"),
                Arguments.of("Throws on parse single value with extra-dots #3", ".12"),
                Arguments.of("Throws on parse triple value with separator", "1,2,,3"),
                Arguments.of("Throws on parse triple value with extra-separator #1", "1,2,3,"),
                Arguments.of("Throws on parse triple value with extra-separator and space #1", "1,2,3, "),
                Arguments.of("Throws on parse triple value with extra-separator #2", ",1,2,3"),
                Arguments.of("Throws on parse triple value with extra-separator and space #2", " ,1,2,3"),
                Arguments.of("Throws on parse triple value with extra-separator and space #3", " ,1,2,3 "),
                Arguments.of("Throws on parse triple value with wrong separator", "1;2"),
                Arguments.of("Throws on parse triple value with wrong separator", "1;2 "),
                Arguments.of("Throws on parse triple value with wrong separator", "1; 2 "),
                Arguments.of("Throws on parse triple value with space-separator", "1 2"),
                Arguments.of("Throws on parse triple value with space-separator and extra-space", "1 2 ")
        );
    }

    @DisplayName("Positive tests")
    @ParameterizedTest(name = "{0}")
    @MethodSource("getPositiveTestData")
    void runPositiveTests(String testName, String input, List<Integer> expected) {
        assertEquals(expected, this.formatter.parseAnswers(input));
    }

    @DisplayName("Negative tests")
    @ParameterizedTest(name = "{0}")
    @MethodSource("getNegativeTestData")
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