package ru.otus.hw.utils.formatters.base;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.utils.formatters.base.contracts.InputFormatter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("native")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultInputStreamFormatter implements InputFormatter {
    static String ANSWER_SPLIT_REGEX = "[,]\\s*";

    static String TEMPLATE_INCORRECT_CONTENT_ERROR = "Not a digit value {0}";

    static String MSG_EMPTY_INPUT_STRING_ERROR = "Empty input string is not a correct answer.";

    String errMsgIncorrectContent;
    String errMsgEmptyInputString;


    /**
     * Служебный конструктор, который позволяет установить
     * шаблоны основных сообщений об ошибках.
     * @param errMsgIncorrectContent    Текст сообщения при вводе некорректного содержимого
     * @param errMsgEmptyInputString    Текст сообщения при вводе пустой/незначащей строки
     */
    protected DefaultInputStreamFormatter(String errMsgIncorrectContent, String errMsgEmptyInputString) {
        this.errMsgIncorrectContent = errMsgIncorrectContent;
        this.errMsgEmptyInputString = errMsgEmptyInputString;
    }

    /**
     * Открытый конструктор для всеобщего использования
     */
    public DefaultInputStreamFormatter() {
        this(TEMPLATE_INCORRECT_CONTENT_ERROR, MSG_EMPTY_INPUT_STRING_ERROR);
    }

    /**
     * Проверка и парсинг введенных пользователем значений
     * @param userInput Ссылка на строку с результатами пользовательского ввода.
     * @return  Числовая последовательность номеров ответов, введенных пользователем.
     * @throws IncorrectAnswerException выбрасывается в случае выявления некорректного ввода.
     */
    @Override
    public List<Integer> parseAnswers(String userInput) throws IncorrectAnswerException {
        if (StringUtils.isBlank(userInput)) {
            throw new IncorrectAnswerException(this.errMsgEmptyInputString);
        }
        var answerTokens = userInput.split(ANSWER_SPLIT_REGEX, -1);
        var incorrectTokens = Arrays.stream(answerTokens)
                                    .filter(t -> !NumberUtils.isDigits(t) || StringUtils.isBlank(t))
                                    .toList();

        if (!incorrectTokens.isEmpty()) {
            var incorrectValue = incorrectTokens.stream()
                    .map(t -> "\"" + t + "\"")
                    .collect(Collectors.joining(",","[ "," ]"));
            String msg = MessageFormat.format(this.errMsgIncorrectContent, incorrectValue);
            throw new IncorrectAnswerException(msg);
        }
        return Arrays.stream(answerTokens)
                .map(NumberUtils::toInt)
                .toList();
    }
}
