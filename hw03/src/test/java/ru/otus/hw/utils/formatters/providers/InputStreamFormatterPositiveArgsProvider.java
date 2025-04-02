package ru.otus.hw.utils.formatters.providers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.stream.Stream;

public class InputStreamFormatterPositiveArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of("Parse single value", "1", List.of(1)),
                Arguments.of("Parse double value with separator", "1,2", List.of(1,2)),
                Arguments.of("Parse double value with separator and space", "1, 2", List.of(1,2))
        );
    }
}
