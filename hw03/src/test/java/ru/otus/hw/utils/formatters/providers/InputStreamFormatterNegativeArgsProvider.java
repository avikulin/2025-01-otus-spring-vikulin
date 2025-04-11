package ru.otus.hw.utils.formatters.providers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InputStreamFormatterNegativeArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
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
}
