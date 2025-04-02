package ru.otus.hw.utils.formatters.localized.output;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestServicePropertiesProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.service.ioservice.config.LocalizedIoStubsConfig;
import ru.otus.hw.utils.formatters.localized.LocalizedOutputStreamFormatterImpl;
import ru.otus.hw.utils.formatters.localized.contracts.LocalizedOutputFormatter;
import ru.otus.hw.utils.formatters.providers.OutputStreamFormatterArgsProvider;
import ru.otus.hw.utils.validators.base.contracts.QuestionValidator;

import static org.mockito.Mockito.*;

@DisplayName("Check the localized output formatting for (ru-RU)")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(classes = LocalizedIoStubsConfig.class)
@ActiveProfiles({"test","localized"})
@TestPropertySource(locations = "classpath:/test-application.yml", properties = {"test.locale=ru_RU"})
@EnableConfigurationProperties({TestServicePropertiesProvider.class})
@Import({LocalizedOutputStreamFormatterImpl.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class LocalizedRuRuOutputStreamFormatterTest {
    // немного дублируем код, но IMHO - это неизбежное зло
    // подробности тут - https://www.yegor256.com/2016/05/03/test-methods-must-share-nothing.html
    static final String MSG_QUESTION_TEMPLATE = "Вопрос: {0}";
    static final String MSG_FIXED_ANSWER_TEMPLATE = "  ⮕ Ответ #{0} : {1}";
    static final String MSG_FREE_USER_ANSWER_TEMPLATE = "  ⮕ Требуется значение от пользователя (в свободной форме)";

    @MockitoBean
    private LocalizedIOService mockIoService;

    @MockitoBean
    private QuestionValidator mockQuestionValidator;

    @Autowired
    private LocalizedOutputFormatter outputFormatter;

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