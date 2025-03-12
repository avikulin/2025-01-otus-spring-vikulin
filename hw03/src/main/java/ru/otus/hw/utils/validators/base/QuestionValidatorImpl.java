package ru.otus.hw.utils.validators.base;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionStateException;
import ru.otus.hw.utils.validators.base.contracts.QuestionValidator;

import java.util.List;
import java.util.Objects;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionValidatorImpl implements QuestionValidator {

    static String MSG_QUESTION_IS_NULL = "Reference to the question must be non-null";

    static String MSG_MAIN_TEMPLATE = "Object [%s] has validation error: %s";

    static String MSG_QUESTION_IS_EMPTY = "Empty or null-valued question is incorrect";

    static String MSG_QUESTION_WITHOUT_ANSWERS = "Question without answers is incorrect";

    static String MSG_QUESTION_WITH_EMPTY_ANSWERS = "Empty or null-valued answer detected";

    static String MSG_QUESTION_WITH_NO_CORRECT_ANSWER = "No correct answer detected";

    String msgMainTemplate;

    String msgErrQuestionIsNull;

    String msgErrQuestionIsEmpty;

    String msgErrQuestionWithoutAnswers;

    String msgErrQuestionWithEmptyAnswers;

    String msgErrQuestionWithNoCorrectAnswers;


    /**
     * Закрытый конструктор для проставления шаблонов выводимых сообщений об ошибках.
     * @param msgMainTemplate   Текст основного шаблона сообщения о том, что присутствуют ошибки.
     * @param msgErrQuestionIsEmpty    Текст вложенного сообщения о пустом объекте вопроса.
     * @param msgErrQuestionWithoutAnswers Текст вложенного сообщения о том, что в вопросе нет ответов.
     * @param msgErrQuestionWithEmptyAnswers   Текст вложенного сообщения о том,
     *                                      что присутствуют пустые значения ответов.
     * @param msgErrQuestionWithNoCorrectAnswers   Текст вложенного сообщения о том,
     *                                          что ни один из ответов не отмечен в качестве правильного.
     */
    protected QuestionValidatorImpl(String msgMainTemplate, String msgErrQuestionIsNull, String msgErrQuestionIsEmpty,
                                    String msgErrQuestionWithoutAnswers, String msgErrQuestionWithEmptyAnswers,
                                    String msgErrQuestionWithNoCorrectAnswers) {
        Validate.notBlank(msgMainTemplate);
        Validate.notBlank(msgErrQuestionIsNull);
        Validate.notBlank(msgErrQuestionIsEmpty);
        Validate.notBlank(msgErrQuestionWithoutAnswers);
        Validate.notBlank(msgErrQuestionWithEmptyAnswers);
        Validate.notBlank(msgErrQuestionWithNoCorrectAnswers);
        this.msgMainTemplate = msgMainTemplate;
        this.msgErrQuestionIsNull = msgErrQuestionIsNull;
        this.msgErrQuestionIsEmpty = msgErrQuestionIsEmpty;
        this.msgErrQuestionWithoutAnswers = msgErrQuestionWithoutAnswers;
        this.msgErrQuestionWithEmptyAnswers = msgErrQuestionWithEmptyAnswers;
        this.msgErrQuestionWithNoCorrectAnswers = msgErrQuestionWithNoCorrectAnswers;
    }


    /**
     * Открытый конструктор для всеобщего использования.
     */
    public QuestionValidatorImpl() {
        this(MSG_MAIN_TEMPLATE, MSG_QUESTION_IS_NULL, MSG_QUESTION_IS_EMPTY, MSG_QUESTION_WITHOUT_ANSWERS,
             MSG_QUESTION_WITH_EMPTY_ANSWERS, MSG_QUESTION_WITH_NO_CORRECT_ANSWER);
    }

    @Override
    public void validateQuestion(Question obj) {
        Objects.requireNonNull(obj, this.msgErrQuestionIsNull);
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
            String errMsg = formatErrMsg(question, this.msgErrQuestionIsEmpty);
            throw new QuestionStateException(errMsg);
        }
    }

    private void checkAnswersPresent(Question question) {
        List<Answer> answers = question.answers();
        var checkAnswersNotPresent = (answers == null || answers.isEmpty());
        if (checkAnswersNotPresent) {
            String errMsg = formatErrMsg(question, this.msgErrQuestionWithoutAnswers);
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
                throw new QuestionStateException(this.msgErrQuestionWithEmptyAnswers);
            }
            if (answer.isCorrect()) {
                correctAnswersCount++;
            }
        }

        if (correctAnswersCount == 0) {
            String errMsg = formatErrMsg(question, this.msgErrQuestionWithNoCorrectAnswers);
            throw new QuestionStateException(errMsg);
        }
    }

    private String formatErrMsg(Question question, String errMsg) {
        return String.format(this.msgMainTemplate, question, errMsg);
    }
}
