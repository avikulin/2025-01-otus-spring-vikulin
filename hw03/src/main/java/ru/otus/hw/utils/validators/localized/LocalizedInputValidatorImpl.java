package ru.otus.hw.utils.validators.localized;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.localization.contracts.LocalizedMessagesService;
import ru.otus.hw.utils.validators.base.DefaultInputValidatorImpl;
import ru.otus.hw.utils.validators.localized.contracts.LocalizedInputValidator;

@Component
@Profile("localized")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalizedInputValidatorImpl extends DefaultInputValidatorImpl implements LocalizedInputValidator {
    static String MSG_CODE_EXCEEDS_THE_VALID_RANGE_ERROR = "input-validator.error.exceeds-valid-range";

    static String MSG_CODE_DOUBLED_VARIANT_ERROR = "input-validator.error.doubled-variant";

    static String MSG_CODE_EMPTY_OPTIONS_ERROR = "input-validator.error.empty-options-set";


    public LocalizedInputValidatorImpl(LocalizedMessagesService localizedMessagesService) {
        super(localizedMessagesService.getMessage(MSG_CODE_EXCEEDS_THE_VALID_RANGE_ERROR),
              localizedMessagesService.getMessage(MSG_CODE_DOUBLED_VARIANT_ERROR),
              localizedMessagesService.getMessage(MSG_CODE_EMPTY_OPTIONS_ERROR)
        );
    }
}
