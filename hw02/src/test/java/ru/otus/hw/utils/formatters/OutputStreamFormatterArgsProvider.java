package ru.otus.hw.utils.formatters;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.stream.Stream;

public class OutputStreamFormatterArgsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext){
        return Stream.of(
                Arguments.of("Question with free answer",
                        new Question("May any answer be correct?",
                                List.of(
                                        new Answer(null,true)
                                )
                        ),
                        true
                ),
                Arguments.of(
                        "Question with one option",
                        new Question("Do I have an exactly one answer?",
                                List.of(
                                        new Answer("Here is the answer",true)
                                )
                        ),
                        false
                ),
                Arguments.of("Question with two options",
                        new Question("X-files main statement",
                                List.of(
                                        new Answer("The Tommyknockers exists",false),
                                        new Answer("The truth is out there",true)
                                )
                        ),
                        false
                )
        );
    }
}
