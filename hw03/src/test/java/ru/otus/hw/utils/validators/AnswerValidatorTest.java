package ru.otus.hw.utils.validators;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.utils.validators.config.ValidatorsContextConfiguration;
import ru.otus.hw.utils.validators.base.contracts.AnswerValidator;
import ru.otus.hw.utils.validators.providers.AnswerValidatorArgsProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:/test-application.yml")
@ContextConfiguration(classes = ValidatorsContextConfiguration.class)
class AnswerValidatorTest {
    @Autowired
    private AnswerValidator validator;

    @DisplayName("Answer validation test")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(AnswerValidatorArgsProvider.class)
    void checkAnswer(String testName, Question question, List<Integer> answers, boolean expected) {
        var result = this.validator.checkAnswer(question, answers);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Throws on empty answer value")
    void checkEmptyAnswer() {
        var question = new Question("some useless question",
                List.of(
                        new Answer("option #1", true),
                        new Answer("option #2", false)
                )
        );
        assertThrows(IncorrectAnswerException.class, () -> this.validator.checkAnswer(question, List.of()));
    }
}