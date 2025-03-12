package ru.otus.hw.service.localization;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.Validate;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.contracts.LocaleConfig;
import ru.otus.hw.service.localization.contracts.LocalizedMessagesService;

import java.util.Locale;
import java.util.Objects;

/**
 * Запилил отдельный бин, чтобы соблюдать границы ответственности.
 * Типа SOLID и все такое...
 */

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalizedMessagesServiceImpl implements LocalizedMessagesService {
    LocaleConfig localeConfig;
    MessageSource messageSource;

    @Override
    public String getMessage(String code, Object... args) {
        Validate.notBlank(code, "Code cannot be empty");
        Validate.notNull(args, "Arguments must not be null");
        Locale locale = localeConfig.getLocale();
        Objects.requireNonNull(locale, "The locale object must not be null");
        return this.messageSource.getMessage(code, args, locale);
    }
}
