package ru.otus.hw.utils.validators;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.IncorrectAnswerException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnswerValidator {
    private static final String ANSWER_SPLIT_REGEX = "(\\s*,\\s*)|(\\s+)";

    /**
     * Проверка вопрос на условие свободного ответа
     *
     * @param question Ссылка на объект вопроса
     * @return true - предполагается свободный ответ (строка), false - предполагается выбор из вариантов (числа)
     */
    public static boolean checkForUserFreeOption(Question question) {
        var answers = question.answers();
        if (answers.isEmpty()) {
            return false;
        }
        var firstAnswer = answers.get(0);
        return (firstAnswer.text() == null && firstAnswer.isCorrect());
    }

    /**
     * Проверка на корректные индексы:
     * - минимальное значение должно быть 1 или более.
     * - максимальное значение не должное превышать количество вариантов в вопросе.
     * - индексы не должны дублироваться
     *
     * @param question  Ссылка на объект вопроса
     * @param answerIdx Ссылка на последовательность номеров ответов
     * @throws IncorrectAnswerException выбрасывается в случае выявления некорректного ввода.
     */
    private  static void checkAnswerIdx(Question question, List<Integer> answerIdx) throws IncorrectAnswerException {
        int maxAnswerIdx = question.answers().size();

        if (answerIdx.isEmpty()) {
            throw new IncorrectAnswerException("Empty options set is incorrect by default");
        }

        var countUnbound = answerIdx.stream()
                                          .filter(x -> x < 1 || x > maxAnswerIdx)
                                          .count();
        if (countUnbound > 0) {
            String msg = String.format("Variant exceeds the valid range: from 1 to %d", maxAnswerIdx);
            throw new IncorrectAnswerException(msg);
        }

        var doubledOptions = answerIdx.stream()
                                              .collect(Collectors.toMap(k->k, v->1, Integer::sum))
                                              .entrySet()
                                              .stream()
                                              .filter(es->es.getValue() > 1)
                                              .map(Map.Entry::getKey)
                                              .toList();

        if (!doubledOptions.isEmpty()) {
            String msg = String.format("Doubled variant is prohibited: [ %s ]",
                                        doubledOptions.stream()
                                                      .map(String::valueOf)
                                                      .collect(Collectors.joining(","))
            );
            throw new IncorrectAnswerException(msg);
        }
    }

    /**
     * Проверка и парсинг введенных пользователем значений
     * @param userInput Ссылка на строку с результатами пользовательского ввода.
     * @return  Числовая последовательность номеров ответов, введенных пользователем.
     * @throws IncorrectAnswerException выбрасывается в случае выявления некорректного ввода.
     */
    private  static List<Integer> parseAnswers(String userInput) throws IncorrectAnswerException {
        var answerTokens = userInput.split(ANSWER_SPLIT_REGEX);
        var incorrectTokens = Arrays
                                            .stream(answerTokens)
                                            .filter(t->!NumberUtils.isDigits(t))
                                            .toList();

        if (!incorrectTokens.isEmpty()){
            String msg = String.format("You have entered incorrect content: %s",
                                        incorrectTokens
                                                .stream()
                                                .map(t->"\""+t+"\"")
                                                .collect(Collectors.joining(",","[ "," ]"))
            );
            throw new IncorrectAnswerException(msg);
        }
        return Arrays.stream(answerTokens)
                     .map(NumberUtils::toInt)
                     .toList();
    }

    /**
     * Проверка пользовательского ввода на соответствие списку корректных ответов
     * @param question  Ссылка на объект вопроса
     * @param answer    Ссылка на результат пользовательского ввода
     * @return          true - среди ответов есть хотя бы один корректны,
     *                  false - среди ответов нет ни одного корректного
     * @throws IncorrectAnswerException Выявлен некорректный ввод
     */
    public static boolean checkAnswer(Question question, String answer)  throws IncorrectAnswerException {
        if (StringUtils.isEmpty(answer)) {
            throw new IncorrectAnswerException("Empty answer content is incorrect");
        }
        if (checkForUserFreeOption(question)) {     // свободный ответ
            // т.е. если пришла непустая строка,
            // то она корректна априори
            return true;
        } else {                                    // номера ответов
            var answerIdx = parseAnswers(answer);
            checkAnswerIdx(question, answerIdx);
            var variants = question.answers();
            var correctAnswersIdx = IntStream
                                                    .range(1, variants.size())
                                                    .filter(i->variants.get(i).isCorrect())
                                                    .boxed()
                                                    .collect(Collectors.toSet());
            return (answerIdx.stream().anyMatch(correctAnswersIdx::contains));
        }
    }
}
