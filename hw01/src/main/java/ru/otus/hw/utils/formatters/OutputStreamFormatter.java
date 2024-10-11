package ru.otus.hw.utils.formatters;

import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.io.IOService;
import ru.otus.hw.utils.validators.QuestionValidator;

import java.util.Objects;

public class OutputStreamFormatter {
    private static final String MSG_QUESTION_TEMPLATE = "Question: %s";

    private static final String MSG_FIXED_ANSWER_TEMPLATE = "  ⮕ Answer #%d : %s";

    private static final String MSG_FREE_USER_ANSWER_TEMPLATE = "  ⮕ Requires user answer (in a free form)";

    public static void questionToStream(Question question, IOService io) {
        // вызываем валидацию и намеренно НЕ ловим исключение:
        // все исключения должны перехватываться на самом верхнем (по стеку вызовов) уровне
        QuestionValidator.validateQuestion(question);
        Objects.requireNonNull(io, "Reference to IOService must be non-null");

        io.printFormattedLine(MSG_QUESTION_TEMPLATE, question.text());

        int answerIdx = 0;
        for (Answer a : question.answers()) {
            answerIdx++;
            if (a.text() == null) {
                io.printLine(MSG_FREE_USER_ANSWER_TEMPLATE);
            } else {
                io.printFormattedLine(MSG_FIXED_ANSWER_TEMPLATE, answerIdx, a.text());
            }
        }
    }
}
