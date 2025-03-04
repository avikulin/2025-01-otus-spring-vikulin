package ru.otus.hw.utils.formatters;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.utils.validators.contract.QuestionValidator;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutputStreamFormatterTest {
    // немного дублируем код, но IMHO - это неизбежное зло
    // подробности тут - https://www.yegor256.com/2016/05/03/test-methods-must-share-nothing.html
    private static final String MSG_QUESTION_TEMPLATE = "Question: %s";
    private static final String MSG_FIXED_ANSWER_TEMPLATE = "  ⮕ Answer #%d : %s";
    private static final String MSG_FREE_USER_ANSWER_TEMPLATE = "  ⮕ Requires user answer (in a free form)";

    @Mock
    private IOService mockIoService;

    @Mock
    private QuestionValidator mockQuestionValidator;

    @InjectMocks
    private OutputStreamFormatter outputFormatter;

    @BeforeEach
    void setUpTest() {
        doNothing().when(mockQuestionValidator).validateQuestion(any(Question.class));
        lenient().doNothing().when(mockIoService).printLine(anyString()); // вызывается не всегда
        doNothing().when(mockIoService)
                .printFormattedLine(anyString(), anyString());
    }

    @AfterEach
    void tierDown() {
        reset(mockQuestionValidator, mockIoService);
    }

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(OutputStreamFormatterArgsProvider.class)
    void questionToStreamOneAnswer(String testName, Question question, boolean isFreeUserAnswer) {
        // подготовка теста
        when(mockQuestionValidator.checkForUserFreeOption(any(Question.class))).thenReturn(isFreeUserAnswer);

        // выполняем тест
        outputFormatter.questionToStream(question);

        // проверяем, что только один раз вызывается валидатор
        verify(mockQuestionValidator, times(1)).validateQuestion(question);

        // проверяем взаимодействие с IO-сервисов
        verify(mockIoService, times(1))
                .printFormattedLine(MSG_QUESTION_TEMPLATE, question.text());

        int answerIdx = 0;
        for (var answer : question.answers()) {
            answerIdx++;
            if (mockQuestionValidator.checkForUserFreeOption(question)) {
                verify(mockIoService, times(1))
                        .printLine(eq(MSG_FREE_USER_ANSWER_TEMPLATE));
            } else {
                verify(mockIoService, times(1))
                        .printFormattedLine(eq(MSG_FIXED_ANSWER_TEMPLATE), eq(answerIdx), eq(answer.text()));
            }
        }
    }

}