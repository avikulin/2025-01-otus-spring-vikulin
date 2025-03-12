package ru.otus.hw.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.contracts.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.contracts.TestService;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.utils.formatters.base.OutputStreamFormatter;
import ru.otus.hw.utils.validators.base.contracts.AnswerValidator;
import ru.otus.hw.utils.validators.base.contracts.QuestionValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Test service behaviour check")
@ExtendWith(MockitoExtension.class)
class TestServiceTest {
    // Небольшое дублирование кода.
    // К сожалению, это необходимое зло: лучше так, чем иметь мутные зависимости...
    private static final String FREE_ANSWER_PROMPT = "Enter answer in a free form \\> ";
    private static final String OPTION_IDX_ANSWER_PROMPT = "Enter index (integer number) of correct answers you choose, " +
                                                           "separated by commas of whitespaces " +
                                                           "(in case of multi-variant answer) \\> ";
    private static final String USER_INVITE_PROMPT = "Please answer the questions below%n";
    private static final String CANT_OBTAIN_THE_ANSWER_ERROR = "Can't obtain the suitable answer from user (max. attempts exceeded)." +
                                                                System.lineSeparator();

    private static final String ERROR_MSG_TEMPLATE = System.lineSeparator() + "You have entered incorrect content";

    private static final String STUDENT_NAME = "name";
    private static final String STUDENT_SURNAME = "surname";

    @Mock
    IOService mockedIoService;

    @Mock
    QuestionDao mockedQuestionDao;

    @Mock(strictness = Mock.Strictness.LENIENT)
    AnswerValidator mockedPositiveAnswerValidator;

    @Mock(strictness = Mock.Strictness.LENIENT)
    AnswerValidator mockedNegativeAnswerValidator;

    @Mock
    QuestionValidator mockedQuestionValidator;

    @Mock
    OutputStreamFormatter mockedOutputStreamFormatter;

    @BeforeEach
    void baseSetUp() {
        when(mockedQuestionDao.findAll()).thenReturn(this.setupQuestions());
        when(mockedQuestionValidator.checkForUserFreeOption(eq(setupQuestions().get(0))))
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false);
        when(mockedQuestionValidator.checkForUserFreeOption(eq(setupQuestions().get(1)))).thenReturn(true);
        when(mockedIoService.readIntForRangeWithPrompt(eq(1), eq(2),
                                                       eq(OPTION_IDX_ANSWER_PROMPT),
                                                       eq(CANT_OBTAIN_THE_ANSWER_ERROR))).thenReturn(List.of(1));
        when(mockedIoService.readStringWithPrompt(eq(FREE_ANSWER_PROMPT))).thenReturn("blah blah blah");
        when(mockedPositiveAnswerValidator.checkAnswer(any(Question.class), anyList())).thenReturn(true);
        when(mockedNegativeAnswerValidator.checkAnswer(any(Question.class), anyList())).thenReturn(false);
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

    private TestService setupInstance(AnswerValidator answerValidator) {
        var realService =  new TestServiceImpl(mockedIoService,
                                               mockedQuestionDao,
                                               mockedOutputStreamFormatter,
                                               answerValidator,
                                               mockedQuestionValidator
        );
        return Mockito.spy(realService);
    }

    void baseTestExecutionFlow(AnswerValidator answerValidator, int expected) {
        // секция выполнения
        var service = setupInstance(answerValidator);
        var student = new Student(STUDENT_NAME, STUDENT_SURNAME);
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
        orderedCalls.verify(mockedIoService).printFormattedLine(eq(USER_INVITE_PROMPT));

        // вывод первого вопроса
        orderedCalls.verify(mockedOutputStreamFormatter).questionToStream(any(Question.class));

        // ввод ответа на первый вопрос
        orderedCalls.verify(mockedIoService).readIntForRangeWithPrompt(eq(1), eq(2),
                                                                       eq(OPTION_IDX_ANSWER_PROMPT),
                                                                       eq(CANT_OBTAIN_THE_ANSWER_ERROR));
        orderedCalls.verify(answerValidator).checkAnswer(any(Question.class), anyList());

        // вывод второго вопроса
        orderedCalls.verify(mockedOutputStreamFormatter).questionToStream(any(Question.class));

        // ввод ответа на второй вопрос
        orderedCalls.verify(mockedIoService).readStringWithPrompt(eq(FREE_ANSWER_PROMPT));

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