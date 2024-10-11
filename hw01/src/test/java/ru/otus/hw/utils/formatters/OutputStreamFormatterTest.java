package ru.otus.hw.utils.formatters;

import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.io.IOService;
import ru.otus.hw.service.ioservice.stub.FakeConsole;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OutputStreamFormatterTest {
    // немного дублируем код, но IMHO - это неизбежное зло
    // подробности тут - https://www.yegor256.com/2016/05/03/test-methods-must-share-nothing.html
    private static final String MSG_QUESTION_PREFIX = "Question: ";

    private static final String MSG_ANSWER_PREFIX = "  ⮕ Answer #";

    private static final String MSG_IO_SERVICE_IS_NULL = "Reference to IOService must be non-null";

    private static final String MSG_QUESTION_IS_NULL = "Reference to question must be non-null";

    private static final String QUESTION_1 = "Do I have an exactly one answer?";

    private static final String QUESTION_1_ANSWER_1 = "Here is the answer";

    private static final String QUESTION_2 = "X-files main statement";

    private static final String QUESTION_2_ANSWER_1 = "The Tommyknockers exists";

    private static final String QUESTION_2_ANSWER_2 = "The truth is out there";

    private static Question questionWithOneAnswer;

    private static Question questionWithTwoAnswers;

    private static IOService ioService;
    private static FakeConsole fakeConsole;

    @BeforeAll
    static void globalSetup() {
        ApplicationContext context = new ClassPathXmlApplicationContext("io-tests/io-test-spring-context.xml");
        ioService = context.getBean(IOService.class);
        fakeConsole = context.getBean(FakeConsole.class);
        questionWithOneAnswer = new Question(QUESTION_1, List.of(new Answer(QUESTION_1_ANSWER_1,true)));
        questionWithTwoAnswers = new Question(QUESTION_2, List.of(new Answer(QUESTION_2_ANSWER_1,false),
                                                                  new Answer(QUESTION_2_ANSWER_2,true)));
    }

    @BeforeEach
    void setUp() {
        fakeConsole.reset();
    }

    @AfterAll
    static void tearDown() {
        fakeConsole.close();
    }

    @Test
    @DisplayName("Null-referenced question")
    void questionToStreamNullReferencedQuestion() {
        Exception ex = assertThrows(
                NullPointerException.class,
                ()-> OutputStreamFormatter.questionToStream(null, ioService)
        );
        assertEquals(MSG_QUESTION_IS_NULL, ex.getMessage());
    }

    @Test
    @DisplayName("Null-referenced IO-service")
    void questionToStreamNullReferencedStream() {
        Exception ex = assertThrows(
                NullPointerException.class,
                ()-> OutputStreamFormatter.questionToStream(questionWithOneAnswer, null)
        );
        assertEquals(MSG_IO_SERVICE_IS_NULL, ex.getMessage());
    }

    @Test
    @DisplayName("Question with one correct answer formatting")
    void questionToStreamOneAnswer() {
        var expectedResult = String.format("%s%s\n%s1 : %s\n", MSG_QUESTION_PREFIX,
                                                               QUESTION_1,
                                                               MSG_ANSWER_PREFIX,
                                                               QUESTION_1_ANSWER_1);

        OutputStreamFormatter.questionToStream(questionWithOneAnswer, ioService);
        assertEquals(expectedResult, fakeConsole.getContent());
    }

    @Test
    @DisplayName("Question with two correct answers formatting")
    void questionToStreamTwoCorrectAnswers() {


        var expectedResult = String.format("%s%s\n%s1 : %s\n%s2 : %s\n", MSG_QUESTION_PREFIX,
                                                                         QUESTION_2,
                                                                         MSG_ANSWER_PREFIX,
                                                                         QUESTION_2_ANSWER_1,
                                                                         MSG_ANSWER_PREFIX,
                                                                         QUESTION_2_ANSWER_2);

        OutputStreamFormatter.questionToStream(questionWithTwoAnswers, ioService);
        assertEquals(expectedResult, fakeConsole.getContent());
    }
}