package ru.otus.hw.service.io;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.contracts.TestPropertiesProvider;
import ru.otus.hw.service.localization.contracts.LocalizedMessagesService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.utils.formatters.localized.contracts.LocalizedInputFormatter;
import ru.otus.hw.utils.validators.localized.contracts.LocalizedInputValidator;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalizedStreamsIOService extends StreamsIOService implements LocalizedIOService {
    static String ERR_INCORRECT_CONTENT_CODE = "streams-io-service.error.incorrect-content";
    static String ERR_TRY_AGAIN_CODE = "streams-io-service.error.try-again";
    static String ERR_UNEXPECTED_CODE = "streams-io-service.error.unexpected";

    LocalizedMessagesService localizedMessagesService;

    public LocalizedStreamsIOService(
                            @Value("#{T(System).out}") PrintStream printStream,
                            @Value("#{T(System).err}") PrintStream errorStream,
                            @Value("#{T(System).in}") InputStream inputStream,
                            LocalizedInputFormatter formatter,
                            LocalizedInputValidator inputValidator,
                            TestPropertiesProvider testPropertiesProvider,
                            LocalizedMessagesService localizedMessagesService) {
        super(printStream,
              errorStream,
              inputStream,
              formatter,
              inputValidator,
              testPropertiesProvider,
              localizedMessagesService.getMessage(ERR_INCORRECT_CONTENT_CODE),
              localizedMessagesService.getMessage(ERR_TRY_AGAIN_CODE),
              localizedMessagesService.getMessage(ERR_UNEXPECTED_CODE)
        );

        this.localizedMessagesService = localizedMessagesService;
    }

    @Override
    public void printErrorLocalized(String code) {
        super.printError(this.localizedMessagesService.getMessage(code));
    }

    @Override
    public void printFormattedErrorLocalized(String code, Object... args) {
        super.printError(this.localizedMessagesService.getMessage(code, args));
    }

    @Override
    public void printLineLocalized(String code) {
        super.printLine(this.localizedMessagesService.getMessage(code));
    }

    @Override
    public void printFormattedLineLocalized(String code, Object... args) {
        super.printLine(this.localizedMessagesService.getMessage(code, args));
    }

    @Override
    public String readStringWithPromptLocalized(String promptCode) {
        String message = this.localizedMessagesService.getMessage(promptCode);
        return super.readStringWithPrompt(message);
    }

    @Override
    public List<Integer> readIntForRangeLocalized(int min, int max, String errorMessageCode) {
        String message = this.localizedMessagesService.getMessage(errorMessageCode);
        return super.readIntForRange(min, max, message);
    }

    @Override
    public List<Integer> readIntForRangeWithPromptLocalized(int min, int max, String promptCode, String errorMessageCode) {
        String msgPrompt = this.localizedMessagesService.getMessage(promptCode);
        String msgError = this.localizedMessagesService.getMessage(errorMessageCode);
        return super.readIntForRangeWithPrompt(min, max, msgPrompt, msgError);
    }
}
