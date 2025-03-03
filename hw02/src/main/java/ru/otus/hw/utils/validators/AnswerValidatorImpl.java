package ru.otus.hw.utils.validators;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.IncorrectAnswerException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerValidatorImpl implements AnswerValidator {
    public static final String MSG_EMPTY_ANSWER_ERROR = "Empty answer content is incorrect";

    QuestionValidator questionValidator;

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
        if (CollectionUtils.isEmpty(answer)) {
            throw new IncorrectAnswerException(MSG_EMPTY_ANSWER_ERROR);
        }
        if (this.questionValidator.checkForUserFreeOption(question)) {    // свободный ответ
                                                                          // т.е. если пришла непустая строка,
                                                                          // то она корректна априори
            return true;
        } else {                                                          // номера ответов
            var variants = question.answers();
            var correctAnswersIdx = IntStream.range(1, variants.size() + 1)
                                             .filter(i->variants.get(i - 1).isCorrect())
                                             .boxed()
                                             .collect(Collectors.toSet());
            return (correctAnswersIdx.containsAll(answer));
        }
    }
}
