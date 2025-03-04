package ru.otus.hw.utils.validators;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.utils.validators.contract.InputValidator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class InputValidatorImpl implements InputValidator {
    private static final String TEMPLATE_EXCEEDS_THE_VALID_RANGE_ERROR = "Variant exceeds the valid range: " +
                                                                         "from %d to %d";

    private static final String TEMPLATE_DOUBLED_VARIANT_ERROR = "Doubled variant is prohibited: [ %s ]";

    private static final String MSG_EMPTY_OPTIONS_ERROR = "Empty options set is incorrect by default";

    /**
     * Проверка на корректные индексы:
     * - больше минимально возможного значения.
     * - меньше максимально возможного значения.
     * - индексы не должны дублироваться
     *
     * @param min Минимально возможное значение индекса ответа
     * @param max Максимально возможное значение индекса ответа
     * @param answerIdx Ссылка на последовательность номеров ответов
     * @throws IncorrectAnswerException выбрасывается в случае выявления некорректного ввода.
     */
    @Override
    public void checkIndexValues(int min, int max, List<Integer> answerIdx) throws IncorrectAnswerException {

        if (answerIdx.isEmpty()) {
            throw new IncorrectAnswerException(MSG_EMPTY_OPTIONS_ERROR);
        }
        this.checkUnboundValues(min, max, answerIdx);
        this.checkDoubledOptions(answerIdx);
    }

    private void checkUnboundValues(int min, int max, List<Integer> answerIdx) {
        var countUnbound = answerIdx
                .stream()
                .filter(x -> x < min || x > max)
                .count();
        if (countUnbound > 0) {
            String msg = String.format(TEMPLATE_EXCEEDS_THE_VALID_RANGE_ERROR, min, max);
            throw new IncorrectAnswerException(msg);
        }
    }

    private void checkDoubledOptions(List<Integer> answerIdx) {
        var doubledOptions = answerIdx.stream()
                .collect(
                        Collectors.toMap(k -> k, v -> 1, Integer::sum)
                )
                .entrySet()
                .stream()
                .filter(es -> es.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (!doubledOptions.isEmpty()) {
            String msg = String.format(TEMPLATE_DOUBLED_VARIANT_ERROR,
                    doubledOptions.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","))
            );
            throw new IncorrectAnswerException(msg);
        }
    }
}
