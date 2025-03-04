package ru.otus.hw.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.contracts.TestConfig;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.io.contracts.IOService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("Showing test results behaviour check")
@ExtendWith(MockitoExtension.class)
class ResultServiceTest {
    //немного копируем код во избежание мутных зависимостей
    public static final String TEMPLATE_STUDENT_INFO = "Student: %s";
    public static final String TEMPLATE_ANSWERED_QUESTIONS_COUNT = "Answered questions count: %d";
    public static final String TEMPLATE_RIGHT_ANSWERS_COUNT = "Right answers count: %d";
    public static final String MSG_CONGRATULATIONS = "Congratulations! You have passed the test!";
    public static final String MSG_TEST_FAILURE = "Sorry. You have failed the test.";


    @Mock
    TestConfig mockedTestConfig;

    @Mock
    IOService mockedIoService;

    @InjectMocks
    ResultServiceImpl resultService;

    @AfterEach
    public void cleanUp(){
        reset(mockedTestConfig, mockedIoService);
    }

    private void baseIoCheckUp(TestResult result, String testResultMessage) {
        var studentsFullName = result.getStudent().getFullName();
        var questionCount = result.getRightAnswersCount();
        var rightAnswersCount = result.getRightAnswersCount();

        InOrder orderedCalls = Mockito.inOrder(mockedIoService, mockedTestConfig);
        orderedCalls.verify(mockedIoService, times(2)).printLine(anyString());
        orderedCalls.verify(mockedIoService).printFormattedLine(eq(TEMPLATE_STUDENT_INFO),eq(studentsFullName));
        orderedCalls.verify(mockedIoService).printFormattedLine(eq(TEMPLATE_ANSWERED_QUESTIONS_COUNT),eq(questionCount));
        orderedCalls.verify(mockedIoService).printFormattedLine(eq(TEMPLATE_RIGHT_ANSWERS_COUNT),eq(rightAnswersCount));
        orderedCalls.verify(mockedIoService).printLine(eq(testResultMessage));
    }

    public static List<Question> sampleQuestionsFactory() {
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

    public static Student sampleStudentFactory(){
        return new Student("name", "surname");
    }

    public static TestResult sampleResultFactory() {
        var student = sampleStudentFactory();
        var questions = sampleQuestionsFactory();
        var result = new TestResult(student);
        questions.forEach(question -> {
            result.applyAnswer(question, true);
        });
        return result;
    }

    @Test
    @DisplayName("Test PASSED situation check")
    void positiveTest() {
        // подготовка данных
        var sampleResult = sampleResultFactory();

        // настройка теста
        when(mockedTestConfig.getRightAnswersCountToPass()).thenReturn(2);

        // выполнение теста
        this.resultService.showResult(sampleResult);

        // проверка результатов
        this.baseIoCheckUp(sampleResult, MSG_CONGRATULATIONS);
    }

    @Test
    @DisplayName("Test FAILED situation check")
    void negativeTest() {
        // подготовка данных
        var sampleResult = sampleResultFactory();

        // настройка теста
        when(mockedTestConfig.getRightAnswersCountToPass()).thenReturn(22);

        // выполнение теста
        this.resultService.showResult(sampleResult);

        // проверка результатов
        this.baseIoCheckUp(sampleResult, MSG_TEST_FAILURE);
    }
}