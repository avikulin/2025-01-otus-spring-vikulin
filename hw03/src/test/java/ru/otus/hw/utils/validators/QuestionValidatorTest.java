package ru.otus.hw.utils.validators;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionStateException;
import ru.otus.hw.utils.validators.base.DefaultQuestionValidatorImpl;
import ru.otus.hw.utils.validators.base.contracts.QuestionValidator;
import ru.otus.hw.utils.validators.config.ValidatorsContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = ValidatorsContextConfiguration.class)
@Import(DefaultQuestionValidatorImpl.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles({"test","native"})
class QuestionValidatorTest {
    private static final String MSG_QUESTION_IS_NULL = "Reference to the question must be non-null";

    @Autowired
    private QuestionValidator validator;

    @Test
    @DisplayName("Null-referenced question")
    void validateNullReferencedQuestion() {
        Exception ex = assertThrows(
                NullPointerException.class,
                ()-> validator.validateQuestion(null)
        );
        assertEquals(MSG_QUESTION_IS_NULL, ex.getMessage());
    }

    @Test
    @DisplayName("Empty question throws error")
    void validateEmptyQuestion() {
        var testQuestion = new Question("",null);

        assertThrows(
                QuestionStateException.class,
                ()-> validator.validateQuestion(testQuestion)
        );
    }

    @Test
    @DisplayName("Empty answers throw error")
    void validateQuestionWithEmptyAnswers() {
        var testQuestion = new Question("I am a question?",
                List.of(
                        new Answer("",true),
                        new Answer("",false)
                )
        );

        assertThrows(
                QuestionStateException.class,
                ()-> validator.validateQuestion(testQuestion)
        );
    }

    @Test
    @DisplayName("Null-values answers throw error")
    void validateQuestionWithNullAnswers() {
        var listOfNulls = new ArrayList<Answer>();
        listOfNulls.add(null);
        listOfNulls.add(null);

        var testQuestion = new Question("Do you want to see a question?",listOfNulls);

        assertThrows(
                QuestionStateException.class,
                ()-> validator.validateQuestion(testQuestion)
        );
    }

    @Test
    @DisplayName("Question with one correct answer does not throw error")
    void validateQuestionWithOneAnswer() {
        var questionStr = "Do I have an exactly one answer?";
        var answerStr = "Here is the answer";

        var testQuestion = new Question(questionStr,
                List.of(
                        new Answer(answerStr,true)
                )
        );

        assertDoesNotThrow(()-> validator.validateQuestion(testQuestion));
    }

    @Test
    @DisplayName("Empty answer among non-empty still throw error")
    void validateQuestionWithOneCorrectAndOneEmptyAnswer() {
        var testQuestion = new Question("Do I have an two answers?",
                List.of(
                        new Answer("Here is the answer",true),
                        new Answer("",false)
                )
        );
        assertThrows(
                QuestionStateException.class,
                ()-> validator.validateQuestion(testQuestion)
        );
    }

    @Test
    @DisplayName("Null-valued answer among non-empty still throw error")
    void validateQuestionWithOneCorrectAndOneNullValuesAnswer() {
        var answers = new ArrayList<Answer>();
        answers.add(new Answer("Here is the answer",true));
        answers.add(null);

        var testQuestion = new Question("Do I have an two answers?",answers);
        assertThrows(
                QuestionStateException.class,
                ()-> validator.validateQuestion(testQuestion)
        );
    }


    @Test
    @DisplayName("Question with two correct answers does not throw error")
    void validateQuestionWithTwoCorrectAnswers() {
        var questionStr = "X-files main statement";
        var answerOneStr = "The Tommyknockers exists";
        var answerTwoStr = "The truth is out there";

        var testQuestion = new Question(questionStr,
                List.of(
                        new Answer(answerOneStr,false),
                        new Answer(answerTwoStr,true)
                )
        );

        assertDoesNotThrow(()-> validator.validateQuestion(testQuestion));
    }

    @Test
    @DisplayName("Question with no correct answer throws error")
    void validateQuestionWithNoCorrectAnswers() {
        var questionStr = "X-files main statement";
        var answerOneStr = "The Tommyknockers exists";
        var answerTwoStr = "The truth is out there";

        var testQuestion = new Question(questionStr,
                List.of(
                        new Answer(answerOneStr,false),
                        new Answer(answerTwoStr,false)
                )
        );

        assertThrows(QuestionStateException.class, ()-> validator.validateQuestion(testQuestion));
    }

    @Test
    @DisplayName("One incorrect null-answer is not a free user answer option ")
    void validateFreeUserOption1(){
        var notFreeAnswer = new Question("Some useless question",
                                            List.of(
                                                    new Answer(null,false)
                                            ));
        assertFalse(this.validator.checkForUserFreeOption(notFreeAnswer));
    }

    @Test
    @DisplayName("One incorrect null-answer & one correct null-answer is not a free user answer option ")
    void validateFreeUserOption2(){
        var notFreeAnswer = new Question("Some useless question",
                List.of(
                        new Answer(null,false),
                        new Answer(null,true)
                ));
        assertFalse(this.validator.checkForUserFreeOption(notFreeAnswer));
    }

    @Test
    @DisplayName("Two correct null-answer is not a free user answer option ")
    void validateFreeUserOption3(){
        var notFreeAnswer = new Question("Some useless question",
                List.of(
                        new Answer(null,true),
                        new Answer(null,true)
                ));
        assertFalse(this.validator.checkForUserFreeOption(notFreeAnswer));
    }

    @Test
    @DisplayName("One correct not-null-answer is not a free user answer option ")
    void validateFreeUserOption4(){
        var notFreeAnswer = new Question("Some useless question",
                List.of(
                        new Answer("fsdfsdf",true)
                ));
        assertFalse(this.validator.checkForUserFreeOption(notFreeAnswer));
    }

    @Test
    @DisplayName("One correct not-null-answer & one correct null-answe is not a free user answer option ")
    void validateFreeUserOption5(){
        var notFreeAnswer = new Question("Some useless question",
                List.of(
                        new Answer(null,true),
                        new Answer("fsdfsdf",true)
                ));
        assertFalse(this.validator.checkForUserFreeOption(notFreeAnswer));
    }

    @Test
    @DisplayName("One correct null-answer is a free user answer option ")
    void validateFreeUserOption6(){
        var notFreeAnswer = new Question("Some useless question",
                List.of(
                        new Answer(null,true)
                ));
        assertTrue(this.validator.checkForUserFreeOption(notFreeAnswer));
    }
}