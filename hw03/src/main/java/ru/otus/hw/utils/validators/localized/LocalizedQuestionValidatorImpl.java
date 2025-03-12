package ru.otus.hw.utils.validators.localized;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.localization.contracts.LocalizedMessagesService;
import ru.otus.hw.utils.validators.base.QuestionValidatorImpl;
import ru.otus.hw.utils.validators.localized.contracts.LocalizedQuestionValidator;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalizedQuestionValidatorImpl extends QuestionValidatorImpl implements LocalizedQuestionValidator {
    static String MSG_CODE_MAIN_TEMPLATE = "question-validator.error.general";

    static String MSG_CODE_QUESTION_IS_NULL = "question-validator.error.null-reference";

    static String MSG_CODE_QUESTION_IS_EMPTY = "question-validator.error.empty-question";

    static String MSG_CODE_QUESTION_WITHOUT_ANSWERS = "question-validator.error.no-answers";

    static String MSG_CODE_QUESTION_WITH_EMPTY_ANSWERS = "question-validator.error.empty-answers";

    static String MSG_CODE_QUESTION_WITH_NO_CORRECT_ANSWER = "question-validator.error.no-correct-answer";


    public LocalizedQuestionValidatorImpl(LocalizedMessagesService localizedMessagesService) {
        super(localizedMessagesService.getMessage(MSG_CODE_MAIN_TEMPLATE),
              localizedMessagesService.getMessage(MSG_CODE_QUESTION_IS_NULL),
              localizedMessagesService.getMessage(MSG_CODE_QUESTION_IS_EMPTY),
              localizedMessagesService.getMessage(MSG_CODE_QUESTION_WITHOUT_ANSWERS),
              localizedMessagesService.getMessage(MSG_CODE_QUESTION_WITH_EMPTY_ANSWERS),
              localizedMessagesService.getMessage(MSG_CODE_QUESTION_WITH_NO_CORRECT_ANSWER)
        );
    }
}
