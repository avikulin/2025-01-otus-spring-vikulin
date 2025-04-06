package ru.otus.hw.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.contracts.ResultService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes= ResultServiceTest.TestConfig.class)
@DisplayName("Showing test results behaviour check")
@ActiveProfiles(profiles = {"test", "localized"})
@FieldDefaults(level = AccessLevel.PRIVATE)
class ResultServiceTest {
    //немного копируем код во избежание мутных зависимостей
    static final String MSG_CODE_TEMPLATE_STUDENT_INFO = "result-service.msg.student-info";
    static final String MSG_CODE_TEMPLATE_ANSWERED_QUESTIONS_COUNT = "result-service.msg.answered-questions-count";
    static final String MSG_CODE_TEMPLATE_RIGHT_ANSWERS_COUNT = "result-service.msg.right-answers-count";
    static final String MSG_CODE_MSG_CONGRATULATIONS = "result-service.msg.congratulations";
    static final String MSG_CODE_MSG_TEST_FAILURE = "result-service.msg.test-failure";

    @Configuration
    @Profile("test")
    @Import({ResultServiceImpl.class})
    static class TestConfig{}

    @MockitoBean
    ru.otus.hw.config.contracts.TestConfig mockedTestConfig;

    @MockitoBean
    LocalizedIOService mockedIoService;

    @Autowired
    ResultService resultService;

    @AfterEach
    public void cleanUp(){
        reset(mockedTestConfig, mockedIoService);
    }

    private void baseIoCheckUp(TestResult result, String testResultMessage) {
        var studentsFullName = result.getStudent().getFullName();
        var questionCount = result.getRightAnswersCount();
        var rightAnswersCount = result.getRightAnswersCount();

        InOrder orderedCalls = Mockito.inOrder(mockedIoService, mockedTestConfig);
        orderedCalls.verify(mockedIoService, times(1)).printLineLocalized(anyString());
        orderedCalls.verify(mockedIoService).printFormattedLineLocalized(eq(MSG_CODE_TEMPLATE_STUDENT_INFO),eq(studentsFullName));
        orderedCalls.verify(mockedIoService).printFormattedLineLocalized(eq(MSG_CODE_TEMPLATE_ANSWERED_QUESTIONS_COUNT),eq(questionCount));
        orderedCalls.verify(mockedIoService).printFormattedLineLocalized(eq(MSG_CODE_TEMPLATE_RIGHT_ANSWERS_COUNT),eq(rightAnswersCount));
        orderedCalls.verify(mockedIoService).printLineLocalized(eq(testResultMessage));
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
        this.baseIoCheckUp(sampleResult, MSG_CODE_MSG_CONGRATULATIONS);
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
        this.baseIoCheckUp(sampleResult, MSG_CODE_MSG_TEST_FAILURE);
    }
}