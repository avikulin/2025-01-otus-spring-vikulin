package ru.otus.hw.utils.validators.localized.answer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.service.ioservice.config.LocalizedIoStubsConfig;
import ru.otus.hw.utils.validators.base.DefaultQuestionValidatorImpl;
import ru.otus.hw.utils.validators.localized.LocalizedAnswerValidatorImpl;
import ru.otus.hw.utils.validators.localized.contracts.LocalizedAnswerValidator;
import ru.otus.hw.utils.validators.providers.AnswerValidatorArgsProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Check the localized (ru-RU) behaviour of answer validation")
@SpringBootTest(classes = LocalizedIoStubsConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(locations = "classpath:/test-application.yml", properties = "test.locale=ru_RU")
@Import({LocalizedAnswerValidatorImpl.class, DefaultQuestionValidatorImpl.class})
@ActiveProfiles({"test","localized"})
class LocalizedRuRuAnswerValidatorTest {
    @Autowired
    private LocalizedAnswerValidator validator;

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