package ru.otus.hw.utils.formatters.localized;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.localization.contracts.LocalizedMessagesService;
import ru.otus.hw.utils.formatters.base.DefaultInputStreamFormatter;
import ru.otus.hw.utils.formatters.localized.contracts.LocalizedInputFormatter;

@Component
@Profile("localized")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalizedInputStreamFormatterImpl extends DefaultInputStreamFormatter implements LocalizedInputFormatter {
    static String MSG_CODE_INCORRECT_CONTENT_ERROR = "input-stream-formatter.error.incorrect-content";
    static String MSG_CODE_EMPTY_INPUT_STRING_ERROR = "InputStreamFormatter.error.empty-input-string";

    public LocalizedInputStreamFormatterImpl(LocalizedMessagesService localizedMessagesService) {
        super(localizedMessagesService.getMessage(MSG_CODE_INCORRECT_CONTENT_ERROR),
              localizedMessagesService.getMessage(MSG_CODE_EMPTY_INPUT_STRING_ERROR));
    }
}
