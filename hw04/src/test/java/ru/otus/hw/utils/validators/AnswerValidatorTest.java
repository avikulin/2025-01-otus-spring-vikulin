package ru.otus.hw.utils.validators;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.utils.validators.base.DefaultAnswerValidatorImpl;
import ru.otus.hw.utils.validators.base.DefaultQuestionValidatorImpl;
import ru.otus.hw.utils.validators.base.contracts.AnswerValidator;
import ru.otus.hw.utils.validators.providers.AnswerValidatorArgsProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Check native behaviour of answer validation")
@SpringBootTest(classes = AnswerValidatorTest.TestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import({DefaultAnswerValidatorImpl.class, DefaultQuestionValidatorImpl.class})
@ActiveProfiles({"test","native"})
class AnswerValidatorTest {
    @Autowired
    private AnswerValidator validator;

    @Configuration
    @Profile("test")
    static class TestConfig{}

    @DisplayName("Correct answers validation tests")
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