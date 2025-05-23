package ru.otus.hw.utils.validators;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionStateException;
import ru.otus.hw.utils.validators.contract.QuestionValidator;

import java.util.List;
import java.util.Objects;

@Component
@NoArgsConstructor
public class QuestionValidatorImpl implements QuestionValidator {

    private static final String MSG_MAIN_TEMPLATE = "Object [%s] has validation error: %s";

    private static final String MSG_QUESTION_IS_EMPTY = "Empty or null-valued question is incorrect";

    private static final String MSG_QUESTION_WITHOUT_ANSWERS = "Question without answers is incorrect";

    private static final String MSG_QUESTION_WITH_EMPTY_ANSWERS = "Empty or null-valued answer detected";

    private static final String MSG_QUESTION_WITH_NO_CORRECT_ANSWER = "No correct answer detected";

    @Override
    public void validateQuestion(Question obj) {
        Objects.requireNonNull(obj, "Reference to question must be non-null");
        checkQuestionPresent(obj);
        checkAnswersPresent(obj);
        checkAnswersHaveValue(obj);
    }

    @Override
    public boolean checkForUserFreeOption(Question question) {
        var answers = question.answers();
        if (answers.size() != 1) {
            return false;
        }
        var firstAnswer = answers.get(0);
        return (firstAnswer.text() == null && firstAnswer.isCorrect());
    }

    private void checkQuestionPresent(Question question) {
        var questionText = question.text();
        var checkQuestionNotPresent = (questionText == null || questionText.isBlank());
        if (checkQuestionNotPresent) {
            String errMsg = formatErrMsg(question, MSG_QUESTION_IS_EMPTY);
            throw new QuestionStateException(errMsg);
        }
    }

    private void checkAnswersPresent(Question question) {
        List<Answer> answers = question.answers();
        var checkAnswersNotPresent = (answers == null || answers.isEmpty());
        if (checkAnswersNotPresent) {
            String errMsg = formatErrMsg(question, MSG_QUESTION_WITHOUT_ANSWERS);
            throw new QuestionStateException(errMsg);
        }
    }

    private void checkAnswersHaveValue(Question question) {
        List<Answer> answers = question.answers();
        if (answers.isEmpty()) {
            return;
        }

        // Тут конечно мы лишний раз пробегаем по коллекции,
        // но без этого валидации не сделать. Неизбежное зло, но без влияния на общую асимптотику.
        int correctAnswersCount = 0;
        for (var answer : answers) {
            if (answer == null || (answer.text() != null && answer.text().isBlank())) {
                throw new QuestionStateException(MSG_QUESTION_WITH_EMPTY_ANSWERS);
            }
            if (answer.isCorrect()) {
                correctAnswersCount++;
            }
        }

        if (correctAnswersCount == 0) {
            String errMsg = formatErrMsg(question,MSG_QUESTION_WITH_NO_CORRECT_ANSWER);
            throw new QuestionStateException(errMsg);
        }
    }

    private String formatErrMsg(Question question, String errMsg) {
        return String.format(MSG_MAIN_TEMPLATE, question, errMsg);
    }
}
