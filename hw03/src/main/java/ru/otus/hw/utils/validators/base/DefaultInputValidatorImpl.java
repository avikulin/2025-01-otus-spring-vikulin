package ru.otus.hw.utils.validators.base;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.Validate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.utils.validators.base.contracts.InputValidator;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Profile("native")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultInputValidatorImpl implements InputValidator {
    static String TEMPLATE_EXCEEDS_THE_VALID_RANGE_ERROR = "Variant exceeds the valid range: from {0} to {1}";

    static String TEMPLATE_DOUBLED_VARIANT_ERROR = "Doubled variant is prohibited: [ {0} ]";

    static String MSG_NULL_ANSWER_COLLECTION = "Input value object must be not-null";

    static String MSG_EMPTY_OPTIONS_ERROR = "Empty options set is incorrect by default";

    String msgErrTemplateExceedsTheValidRange;

    String msgErrTemplateDoubledVariant;

    String msgErrEmptyOptions;


    /**
     * Закрытый служебный конструктор для проставления шаблоново текста сообщений об ошибках.
     * @param msgErrTemplateExceedsTheValidRange    Текст ошибки о выходе номера варианта за допустимые границы.
     * @param msgErrTemplateDoubledVariant  Текст ошибки о вводе более одного уникального значения варианта.
     * @param msgErrEmptyOptions    Текст ошибки о вводе пустого/незначащего значения варианта.
     */
    protected DefaultInputValidatorImpl(String msgErrTemplateExceedsTheValidRange,
                                        String msgErrTemplateDoubledVariant,
                                        String msgErrEmptyOptions) {
        Validate.notBlank(msgErrTemplateExceedsTheValidRange);
        Validate.notBlank(msgErrTemplateDoubledVariant);
        Validate.notBlank(msgErrEmptyOptions);
        this.msgErrTemplateExceedsTheValidRange = msgErrTemplateExceedsTheValidRange;
        this.msgErrTemplateDoubledVariant = msgErrTemplateDoubledVariant;
        this.msgErrEmptyOptions = msgErrEmptyOptions;

    }


    /**
     * Публичный конструктор для всеобщего использования.
     */
    public DefaultInputValidatorImpl() {
        this(TEMPLATE_EXCEEDS_THE_VALID_RANGE_ERROR, TEMPLATE_DOUBLED_VARIANT_ERROR, MSG_EMPTY_OPTIONS_ERROR);
    }

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
        Objects.requireNonNull(answerIdx, MSG_NULL_ANSWER_COLLECTION);
        if (answerIdx.isEmpty()) {
            throw new IncorrectAnswerException(this.msgErrEmptyOptions);
        }
        this.checkUnboundValues(min, max, answerIdx);
        this.checkDoubledOptions(answerIdx);
    }

    private void checkUnboundValues(int min, int max, List<Integer> answerIdx) {
        Objects.requireNonNull(answerIdx, MSG_NULL_ANSWER_COLLECTION);
        var countUnbound = answerIdx
                .stream()
                .filter(x -> x < min || x > max)
                .count();
        if (countUnbound > 0) {
            String msg = MessageFormat.format(this.msgErrTemplateExceedsTheValidRange, min, max);
            throw new IncorrectAnswerException(msg);
        }
    }

    private void checkDoubledOptions(List<Integer> answerIdx) {
        Objects.requireNonNull(answerIdx, MSG_NULL_ANSWER_COLLECTION);
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
            String msg = MessageFormat.format(this.msgErrTemplateDoubledVariant,
                    doubledOptions.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","))
            );
            throw new IncorrectAnswerException(msg);
        }
    }
}
