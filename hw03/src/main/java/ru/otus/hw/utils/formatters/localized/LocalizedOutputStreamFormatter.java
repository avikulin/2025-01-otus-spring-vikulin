package ru.otus.hw.utils.formatters.localized;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.localization.contracts.LocalizedMessagesService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.utils.formatters.base.OutputStreamFormatter;
import ru.otus.hw.utils.formatters.localized.contracts.LocalizedOutputFormatter;
import ru.otus.hw.utils.validators.localized.contracts.LocalizedQuestionValidator;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalizedOutputStreamFormatter extends OutputStreamFormatter implements LocalizedOutputFormatter {
    static String MSG_CODE_QUESTION_TEMPLATE = "output-stream-formatter.msg.question-id";

    static String MSG_CODE_FIXED_ANSWER_TEMPLATE = "output-stream-formatter.msg.fixed-index-answer";

    static String MSG_CODE_FREE_USER_ANSWER_TEMPLATE = "output-stream-formatter.msg.free-answer";

    static String MSG_CODE_NULL_QUESTION_ERROR = "output-stream-formatter.error.null-reference";

    public LocalizedOutputStreamFormatter(LocalizedIOService ioService,
                                          LocalizedQuestionValidator questionValidator,
                                          LocalizedMessagesService localizedMessagesService) {
        super(ioService, questionValidator, localizedMessagesService.getMessage(MSG_CODE_QUESTION_TEMPLATE),
              localizedMessagesService.getMessage(MSG_CODE_FIXED_ANSWER_TEMPLATE),
              localizedMessagesService.getMessage(MSG_CODE_FREE_USER_ANSWER_TEMPLATE),
              localizedMessagesService.getMessage(MSG_CODE_NULL_QUESTION_ERROR));
    }
}
