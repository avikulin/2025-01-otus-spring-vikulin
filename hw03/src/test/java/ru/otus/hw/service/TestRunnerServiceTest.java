package ru.otus.hw.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.exceptions.QuestionStateException;
import ru.otus.hw.service.contracts.ResultService;
import ru.otus.hw.service.contracts.StudentService;
import ru.otus.hw.service.contracts.TestRunnerService;
import ru.otus.hw.service.contracts.TestService;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

@DisplayName("Test runner behaviour check")
@SpringBootTest(classes = TestRunnerServiceTest.TestConfig.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ActiveProfiles(profiles = {"test", "localized"})
class TestRunnerServiceTest {
    // немного дублируем код во избежание мутных зависимостей
    static final String MSG_QUESTION_READ_EXCEPTION  = "The internal error appeared during the load " +
                                                       "of the test configuration file with question";
    static final String MSG_QUESTION_STATE_EXCEPTION = "The incorrect question found inside " +
                                                       "the test configuration file";

    static final String MSG_UNKNOWN_ERROR = "Unknown error occurred. See the log file for details";

    @Configuration
    @Import(TestRunnerServiceImpl.class)
    @Profile("test")
    static class TestConfig{}

    @MockitoBean
    TestService testService;

    @MockitoBean
    StudentService studentService;

    @MockitoBean
    ResultService resultService;

    @MockitoBean
    LocalizedIOService ioService;

    @Autowired
    TestRunnerService testRunnerService;

    @BeforeEach
    public void setupTest(){
        lenient().when(studentService.determineCurrentStudent()).thenReturn(sampleStudentFactory());
        lenient().when(testService.executeTestFor(any(Student.class))).thenReturn(sampleResultFactory());
    }

    @AfterEach
    public void cleanUp(){
        reset(testService,studentService,resultService,ioService);
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
    @DisplayName("Check normal behaviour")
    void positiveTest() {
        // выполнение теста
        testRunnerService.run();

        // проверка результата
        InOrder orderedCalls = Mockito.inOrder(testService, studentService, resultService, ioService);
        orderedCalls.verify(studentService).determineCurrentStudent();
        orderedCalls.verify(testService).executeTestFor(any(Student.class));
        orderedCalls.verify(resultService).showResult(any(TestResult.class));
        orderedCalls.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Prints to stderr on <QuestionReadException>")
    void negativeTestQuestionReadException() {
        // подготовка теста
        when(testService.executeTestFor(any(Student.class))).thenThrow(QuestionReadException.class);

        // выполнение теста
        testRunnerService.run();

        // проверка результатов
        verify(ioService, Mockito.times(1)).printError(startsWith(MSG_QUESTION_READ_EXCEPTION));
    }

    @Test
    @DisplayName("Prints to stderr on <QuestionReadException>")
    void negativeTestQuestionStateException() {
        // подготовка теста
        when(testService.executeTestFor(any(Student.class))).thenThrow(QuestionStateException.class);

        // выполнение теста
        testRunnerService.run();

        // проверка результатов
        verify(ioService, Mockito.times(1)).printError(startsWith(MSG_QUESTION_STATE_EXCEPTION));
    }

    @Test
    @DisplayName("Prints to stderr on any unknown exception")
    void negativeTestUnknownException() {
        // подготовка теста
        when(testService.executeTestFor(any(Student.class))).thenThrow(OutOfMemoryError.class);
        // выполнение теста
        testRunnerService.run();
        // проверка результатов
        verify(ioService, Mockito.times(1)).printError(startsWith(MSG_UNKNOWN_ERROR));
    }
}