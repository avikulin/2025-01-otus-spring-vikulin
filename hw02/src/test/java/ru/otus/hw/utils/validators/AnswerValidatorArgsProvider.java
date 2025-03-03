package ru.otus.hw.utils.validators;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.stream.Stream;

public class AnswerValidatorArgsProvider implements ArgumentsProvider {
    Question oneOptionQuestion1 = new Question("Some useless question",
                                                    List.of(
                                                            new Answer("option",false)
                                                    )
    );
    Question oneOptionQuestion2 = new Question("Some useless question",
                                                    List.of(
                                                            new Answer("option",true)
                                                    )
    );

    Question twoOptionsQuestion1 = new Question("Some useless question",
                                                    List.of(
                                                            new Answer("option #1",false),
                                                            new Answer("option #2",false)
                                                    )
    );

    Question twoOptionsQuestion2 = new Question("Some useless question",
                                                    List.of(
                                                            new Answer("option #1",true),
                                                            new Answer("option #2",false)
                                                    )
    );

    Question twoOptionsQuestion3 = new Question("Some useless question",
                                                    List.of(
                                                            new Answer("option #1",true),
                                                            new Answer("option #2",true)
                                                    )
    );

    Question freeUserAnswerOptionsQuestion = new Question("Some useless question",
            List.of(
                    new Answer(null,true)
            )
    );

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of("Choose sole incorrect answer", oneOptionQuestion1, List.of(1), false),
                Arguments.of("Choose sole correct answer", oneOptionQuestion2, List.of(1), true),
                Arguments.of("Choose first of 2 incorrect answers", twoOptionsQuestion1, List.of(1), false),
                Arguments.of("Choose second of 2 incorrect answers", twoOptionsQuestion1, List.of(2), false),
                Arguments.of("Choose both of 2 incorrect answers", twoOptionsQuestion1, List.of(1,2), false),
                Arguments.of("Choose correct answer from 2 option", twoOptionsQuestion2, List.of(1), true),
                Arguments.of("Choose incorrect answer from 2 option", twoOptionsQuestion2, List.of(2), false),
                Arguments.of("Choose both of correct & incorrect options", twoOptionsQuestion2, List.of(1,2), false),
                Arguments.of("Choose only first answer from 2 correct  option", twoOptionsQuestion3, List.of(1), true),
                Arguments.of("Choose only second answer from 2 correct  option", twoOptionsQuestion3, List.of(2), true),
                Arguments.of("Choose both of correct options", twoOptionsQuestion3, List.of(1,2), true)
        );
    }
}
