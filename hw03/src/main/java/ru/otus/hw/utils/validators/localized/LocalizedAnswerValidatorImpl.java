package ru.otus.hw.utils.validators.localized;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.localization.contracts.LocalizedMessagesService;
import ru.otus.hw.utils.validators.base.AnswerValidatorImpl;
import ru.otus.hw.utils.validators.localized.contracts.LocalizedAnswerValidator;
import ru.otus.hw.utils.validators.localized.contracts.LocalizedQuestionValidator;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalizedAnswerValidatorImpl extends AnswerValidatorImpl implements LocalizedAnswerValidator{
    static String MSG_CODE_ERR_QUESTION_NULL_REFERENCE = "answer-validator.error.null-reference";
    static String MSG_CODE_ERR_EMPTY_ANSWER_COLLECTION = "answer-validator.error.empty-answer";

    protected LocalizedAnswerValidatorImpl(LocalizedQuestionValidator questionValidator,
                                           LocalizedMessagesService localizedMessagesService) {
        super(questionValidator,
              localizedMessagesService.getMessage(MSG_CODE_ERR_QUESTION_NULL_REFERENCE),
              localizedMessagesService.getMessage(MSG_CODE_ERR_EMPTY_ANSWER_COLLECTION)
        );
    }
}
