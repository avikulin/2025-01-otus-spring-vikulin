package ru.otus.hw.utils.validators.localized;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.localization.contracts.LocalizedMessagesService;
import ru.otus.hw.utils.validators.base.DefaultAnswerValidatorImpl;
import ru.otus.hw.utils.validators.base.contracts.QuestionValidator;
import ru.otus.hw.utils.validators.localized.contracts.LocalizedAnswerValidator;

@Component
@Profile("localized")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalizedAnswerValidatorImpl extends DefaultAnswerValidatorImpl implements LocalizedAnswerValidator {
    static String MSG_CODE_ERR_EMPTY_ANSWER_COLLECTION = "answer-validator.error.empty-answer";

    protected LocalizedAnswerValidatorImpl(QuestionValidator questionValidator,
                                           LocalizedMessagesService localizedMessagesService) {
        super(questionValidator, localizedMessagesService.getMessage(MSG_CODE_ERR_EMPTY_ANSWER_COLLECTION));
    }
}
