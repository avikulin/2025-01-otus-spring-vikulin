package ru.otus.hw.utils.validators.base;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.utils.validators.base.contracts.AnswerValidator;
import ru.otus.hw.utils.validators.base.contracts.QuestionValidator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@Profile("native")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultAnswerValidatorImpl implements AnswerValidator {
    static String MSG_QUESTION_NULL_REFERENCE_ERROR = "Reference to the answer object must be non-null";

    static String MSG_EMPTY_ANSWER_COLLECTION_ERROR = "Empty answer content is incorrect";

    QuestionValidator questionValidator;

    String msgErrEmptyAnswersCollection;

    /**
     * Закрытый конструктор для проставления шаблонов сообщений об ошибках.
     * @param questionValidator Ссылка на валидатор вопросов
     * @param msgErrEmptyAnswersCollection  Текст сообщения о пустой коллекции ответов
     */
    protected DefaultAnswerValidatorImpl(QuestionValidator questionValidator,
                                         String msgErrEmptyAnswersCollection) {
        Objects.requireNonNull(questionValidator);
        Validate.notBlank(msgErrEmptyAnswersCollection);
        this.questionValidator = questionValidator;
        this.msgErrEmptyAnswersCollection = msgErrEmptyAnswersCollection;
    }

    /**
     * Открытый конструктор для всеобщего использования
     * @param questionValidator Ссылка на валидатор вопросов
     */
    @Autowired
    public DefaultAnswerValidatorImpl(QuestionValidator questionValidator) {
        this(questionValidator, MSG_EMPTY_ANSWER_COLLECTION_ERROR);
    }

    /**
     * Проверка пользовательского ввода на соответствие списку корректных ответов
     * @param question  Ссылка на объект вопроса
     * @param answer    Ссылка на результат пользовательского ввода
     * @return          true - среди ответов есть хотя бы один корректный,
     *                  false - среди ответов нет ни одного корректного
     * @throws IncorrectAnswerException Выявлен некорректный ввод
     */
    @Override
    public boolean checkAnswer(Question question, List<Integer> answer)  throws IncorrectAnswerException {
        Objects.requireNonNull(question, MSG_QUESTION_NULL_REFERENCE_ERROR);
        if (CollectionUtils.isEmpty(answer)) {
            log.error(MSG_EMPTY_ANSWER_COLLECTION_ERROR);
            throw new IncorrectAnswerException(this.msgErrEmptyAnswersCollection);
        }
        if (this.questionValidator.checkForUserFreeOption(question)) {    // свободный ответ
                                                                          // т.е. если пришла непустая строка,
                                                                          // то она корректна априори
            return true;
        } else {                                                          // номера ответов
            var variants = question.answers();
            var correctAnswersIdx = IntStream.range(1, variants.size() + 1)
                                             .filter(i -> variants.get(i - 1).isCorrect())
                                             .boxed()
                                             .collect(Collectors.toSet());
            return (correctAnswersIdx.containsAll(answer));
        }
    }
}
