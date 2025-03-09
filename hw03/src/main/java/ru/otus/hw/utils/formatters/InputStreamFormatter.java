package ru.otus.hw.utils.formatters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.utils.formatters.contracts.InputFormatter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InputStreamFormatter implements InputFormatter {
    private static final String ANSWER_SPLIT_REGEX = "[,]\\s*";

    private static final String TEMPLATE_INCORRECT_CONTENT_ERROR = "Not a digit value %s";

    private static final String MSG_EMPTY_INPUT_STRING_ERROR = "Empty input string is not a correct answer.";

    String errMsgIncorrectContent;
    String errMsgEmptyInputString;


    /**
     * Служебный конструктор, который позволяет установить
     * шаблоны основных сообщений об ошибках.
     * @param errMsgIncorrectContent
     * @param errMsgEmptyInputString
     */
    protected InputStreamFormatter(String errMsgIncorrectContent, String errMsgEmptyInputString) {
        this.errMsgIncorrectContent = errMsgIncorrectContent;
        this.errMsgEmptyInputString = errMsgEmptyInputString;
    }

    public InputStreamFormatter() {
        this(MSG_EMPTY_INPUT_STRING_ERROR, MSG_EMPTY_INPUT_STRING_ERROR);
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
            String msg = String.format(this.errMsgIncorrectContent,
                                        incorrectTokens.stream()
                                                       .map(t -> "\"" + t + "\"")
                                                       .collect(Collectors.joining(",","[ "," ]"))
            );
            throw new IncorrectAnswerException(msg);
        }
        return Arrays.stream(answerTokens)
                .map(NumberUtils::toInt)
                .toList();
    }
}
