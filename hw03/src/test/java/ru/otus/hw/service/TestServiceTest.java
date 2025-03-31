package ru.otus.hw.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.otus.hw.dao.contracts.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.contracts.TestService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.utils.formatters.localized.LocalizedOutputStreamFormatterImpl;
import ru.otus.hw.utils.validators.base.contracts.QuestionValidator;
import ru.otus.hw.utils.validators.localized.contracts.LocalizedAnswerValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Test service behaviour check")
@SpringBootTest(classes = TestServiceTest.TestConfig.class)
@ActiveProfiles(profiles = {"test", "localized"})
@ExtendWith(MockitoExtension.class)
class TestServiceTest {
    // Небольшое дублирование кода.
    // К сожалению, это необходимое зло: лучше так, чем иметь мутные зависимости...
    static String MSG_CODE_FREE_ANSWER_PROMPT = "test-service.msg.prompt.free-answer";

    static String MSG_CODE_OPTION_IDX_ANSWER_PROMPT = "test-service.msg.prompt.fixed-index-answer";

    static String MSG_CODE_USER_INVITE_PROMPT = "test-service.msg.prompt.user-invite";

    static String MSG_CODE_CANT_OBTAIN_THE_ANSWER_ERROR = "test-service.error.cant-obtain-answer";

    private static final String STUDENT_NAME = "name";
    private static final String STUDENT_SURNAME = "surname";

    @Configuration
    @Profile({"test", "localized"})
    static class TestConfig{}

    @MockitoBean
    LocalizedIOService mockedIoService;

    @MockitoBean
    QuestionDao mockedQuestionDao;

    @MockitoBean
    LocalizedAnswerValidator mockedPositiveAnswerValidator;

    @MockitoBean
    LocalizedAnswerValidator mockedNegativeAnswerValidator;

    @MockitoBean
    QuestionValidator mockedQuestionValidator;

    @MockitoBean
    LocalizedOutputStreamFormatterImpl mockedOutputStreamFormatter;

    @BeforeEach
    void baseSetUp() {
        lenient().when(mockedQuestionDao.findAll()).thenReturn(this.setupQuestions());
        lenient().when(mockedQuestionValidator.checkForUserFreeOption(eq(setupQuestions().get(0))))
                 .thenReturn(false)
                 .thenReturn(false)
                 .thenReturn(false);
        lenient().when(mockedQuestionValidator.checkForUserFreeOption(eq(setupQuestions().get(1)))).thenReturn(true);
        lenient().when(mockedIoService.readIntForRangeWithPrompt(eq(1), eq(2),
                                                       eq(MSG_CODE_OPTION_IDX_ANSWER_PROMPT),
                                                       eq(MSG_CODE_CANT_OBTAIN_THE_ANSWER_ERROR))).thenReturn(List.of(1));
        lenient().when(mockedIoService.readStringWithPrompt(eq(MSG_CODE_FREE_ANSWER_PROMPT))).thenReturn("blah blah blah");
        lenient().when(mockedPositiveAnswerValidator.checkAnswer(any(Question.class), anyList())).thenReturn(true);
        lenient().when(mockedNegativeAnswerValidator.checkAnswer(any(Question.class), anyList())).thenReturn(false);
    }

    @AfterEach
    public void cleanUp(){
        reset(mockedIoService);
        reset(mockedQuestionDao);
        reset(mockedPositiveAnswerValidator);
        reset(mockedNegativeAnswerValidator);
        reset(mockedQuestionValidator);
        reset(mockedOutputStreamFormatter);
    }

    private List<Question> setupQuestions() {
        return List.of(
                new Question("some useless question",
                        List.of(
                                new Answer("option #1", true),
                                new Answer("option #2", false)
                        )
                ),
                new Question("some another useless mention poll",
                        List.of(
                                new Answer(null, true) // free answer option
                        )
                )
        );
    }

    private TestService setupInstance(LocalizedAnswerValidator answerValidator) {
        var realService =  new TestServiceImpl(mockedQuestionDao,
                                               mockedIoService,
                                               mockedOutputStreamFormatter,
                                               answerValidator,
                                               mockedQuestionValidator
        );
        return Mockito.spy(realService);
    }

    void baseTestExecutionFlow(LocalizedAnswerValidator answerValidator, int expected) {
        // секция выполнения

        var student = new Student(STUDENT_NAME, STUDENT_SURNAME);
        var service = setupInstance(answerValidator);
        var testResult = service.executeTestFor(student);

        // секция проверок
        var orderedCalls = inOrder(mockedQuestionDao, mockedIoService,
                                   mockedOutputStreamFormatter, answerValidator,
                                   service
        );

        // зачитывание вопросов
        orderedCalls.verify(mockedQuestionDao).findAll();

        // ввод данных студента
        orderedCalls.verify(mockedIoService).printEmptyLine();
        orderedCalls.verify(mockedIoService).printLineLocalized(eq(MSG_CODE_USER_INVITE_PROMPT));

        // вывод первого вопроса
        orderedCalls.verify(mockedOutputStreamFormatter).questionToStream(any(Question.class));

        // ввод ответа на первый вопрос
        orderedCalls.verify(mockedIoService).readIntForRangeWithPromptLocalized(eq(1), eq(2),
                                                                       eq(MSG_CODE_OPTION_IDX_ANSWER_PROMPT),
                                                                       eq(MSG_CODE_CANT_OBTAIN_THE_ANSWER_ERROR));
        orderedCalls.verify(answerValidator).checkAnswer(any(Question.class), anyList());

        // вывод второго вопроса
        orderedCalls.verify(mockedOutputStreamFormatter).questionToStream(any(Question.class));

        // ввод ответа на второй вопрос
        orderedCalls.verify(mockedIoService).readStringWithPromptLocalized(eq(MSG_CODE_FREE_ANSWER_PROMPT));

        // контроль результатов
        orderedCalls.verifyNoMoreInteractions();
        assertEquals(student, testResult.getStudent());
        assertEquals(expected,testResult.getRightAnswersCount());
        assertEquals(this.setupQuestions(), testResult.getAnsweredQuestions());
    }

    @Test
    @DisplayName("Behavior check for PASSED test examination")
    void positiveTest(){
        this.baseTestExecutionFlow(mockedPositiveAnswerValidator, 2);
    }

    @Test
    @DisplayName("Behavior check for FAILED test examination")
    void negativeTest(){
        this.baseTestExecutionFlow(mockedNegativeAnswerValidator, 1);
    }
}