package ru.otus.hw.utils.formatters.base;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.utils.formatters.base.contracts.OutputFormatter;
import ru.otus.hw.utils.validators.base.contracts.QuestionValidator;

import java.util.Objects;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OutputStreamFormatter implements OutputFormatter {
    static String MSG_QUESTION_TEMPLATE = "Question: %s";

    static String MSG_FIXED_ANSWER_TEMPLATE = "  ⮕ Answer #%d : %s";

    static String MSG_FREE_USER_ANSWER_TEMPLATE = "  ⮕ Requires user answer (in a free form)";

    static String MSG_NULL_QUESTION_ERROR = "Reference to the output value object must be non-null";

    IOService ioService;

    QuestionValidator questionValidator;

    String msgQuestionTemplate;

    String msgFixedAnswerTemplate;

    String msgFreeUserAnswerTemplate;

    String msgErrNullQuestion;

    /**
     * Служебный закрытый конструктор. Используется для простановки шаблонов сообщений об ошибках.
     * @param ioService Ссылка на сервис консольного ввода-вывода
     * @param questionValidator Ссылка на валидатор вопросов
     * @param msgQuestionTemplate   Текст шаблона вывода вопроса
     * @param msgFixedAnswerTemplate    Текст запроса фиксированного ответа (по номерам) у пользователя
     * @param msgFreeUserAnswerTemplate Текст запроса свободного ответа у пользователя
     * @param msgErrNullQuestion    Текст ошибки, если ссылка на объект вопроса не задана
     */
    protected OutputStreamFormatter(IOService ioService, QuestionValidator questionValidator,
                                    String msgQuestionTemplate, String msgFixedAnswerTemplate,
                                    String msgFreeUserAnswerTemplate, String msgErrNullQuestion) {
        Objects.requireNonNull(ioService);
        Objects.requireNonNull(questionValidator);
        Validate.notBlank(msgQuestionTemplate);
        Validate.notBlank(msgFixedAnswerTemplate);
        Validate.notBlank(msgFreeUserAnswerTemplate);
        Validate.notBlank(msgErrNullQuestion);

        this.ioService = ioService;
        this.questionValidator = questionValidator;
        this.msgQuestionTemplate = msgQuestionTemplate;
        this.msgFixedAnswerTemplate = msgFixedAnswerTemplate;
        this.msgFreeUserAnswerTemplate = msgFreeUserAnswerTemplate;
        this.msgErrNullQuestion = msgErrNullQuestion;
    }


    /**
     * Открытый конструктор для всеобщего использования
     * @param ioService Ссылка на сервис консольного ввода-вывода
     * @param questionValidator Ссылка на валидатор вопросов
     */
    @Autowired
    public OutputStreamFormatter(@Qualifier("streamsIOService") IOService ioService,
                                 @Qualifier("questionValidatorImpl") QuestionValidator questionValidator) {
        this(ioService, questionValidator,
             MSG_QUESTION_TEMPLATE, MSG_FIXED_ANSWER_TEMPLATE,
             MSG_FREE_USER_ANSWER_TEMPLATE, MSG_NULL_QUESTION_ERROR);
    }

    @Override
    public void questionToStream(Question question) {
        // вызываем валидацию и намеренно НЕ ловим исключение:
        // все исключения должны перехватываться на самом верхнем (по стеку вызовов) уровне
        questionValidator.validateQuestion(question);
        Objects.requireNonNull(ioService, this.msgErrNullQuestion);

        ioService.printFormattedLine(this.msgQuestionTemplate, question.text());

        int answerIdx = 0;
        for (Answer a : question.answers()) {
            answerIdx++;
            if (questionValidator.checkForUserFreeOption(question)) {
                ioService.printLine(this.msgFreeUserAnswerTemplate);
            } else {
                ioService.printFormattedLine(this.msgFixedAnswerTemplate, answerIdx, a.text());
            }
        }
    }
}
