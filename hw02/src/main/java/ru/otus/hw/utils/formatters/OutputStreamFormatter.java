package ru.otus.hw.utils.formatters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.utils.formatters.contracts.OutputFormatter;
import ru.otus.hw.utils.validators.contract.QuestionValidator;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OutputStreamFormatter implements OutputFormatter {
    private static final String MSG_QUESTION_TEMPLATE = "Question: %s";

    private static final String MSG_FIXED_ANSWER_TEMPLATE = "  ⮕ Answer #%d : %s";

    private static final String MSG_FREE_USER_ANSWER_TEMPLATE = "  ⮕ Requires user answer (in a free form)";

    private final IOService ioService;

    private final QuestionValidator questionValidator;

    @Override
    public void questionToStream(Question question) {
        // вызываем валидацию и намеренно НЕ ловим исключение:
        // все исключения должны перехватываться на самом верхнем (по стеку вызовов) уровне
        questionValidator.validateQuestion(question);
        Objects.requireNonNull(ioService, "Reference to IOService must be non-null");

        ioService.printFormattedLine(MSG_QUESTION_TEMPLATE, question.text());

        int answerIdx = 0;
        for (Answer a : question.answers()) {
            answerIdx++;
            if (questionValidator.checkForUserFreeOption(question)) {
                ioService.printLine(MSG_FREE_USER_ANSWER_TEMPLATE);
            } else {
                ioService.printFormattedLine(MSG_FIXED_ANSWER_TEMPLATE, answerIdx, a.text());
            }
        }
    }
}
