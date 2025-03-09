package ru.otus.hw.service.io;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.contracts.TestConfiguration;
import ru.otus.hw.service.contracts.LocalizedMessagesService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.utils.formatters.contracts.InputFormatter;
import ru.otus.hw.utils.validators.contract.InputValidator;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalizedStreamsIOService extends StreamsIOService implements LocalizedIOService {
    private static final String ERR_INCORRECT_CONTENT_CODE = "streams-io-service.error.incorrect-content";
    private static final String ERR_TRY_AGAIN_CODE = "streams-io-service.error.try-again";

    LocalizedMessagesService localizedMessagesService;

    public LocalizedStreamsIOService(
                            @Value("#{T(System).out}") PrintStream printStream,
                            @Value("#{T(System).err}") PrintStream errorStream,
                            @Value("#{T(System).in}") InputStream inputStream,
                            InputFormatter formatter,
                            InputValidator inputValidator,
                            TestConfiguration testConfiguration,
                            LocalizedMessagesService localizedMessagesService) {
        super(printStream, errorStream, inputStream,
              formatter, inputValidator, testConfiguration,
                localizedMessagesService.getMessage(ERR_INCORRECT_CONTENT_CODE),
                localizedMessagesService.getMessage(ERR_TRY_AGAIN_CODE)
        );

        this.localizedMessagesService = localizedMessagesService;
    }

    @Override
    public void printLineLocalized(String code) {
        super.printLine(localizedMessagesService.getMessage(code));
    }

    @Override
    public void printFormattedLineLocalized(String code, Object... args) {
        super.printLine(localizedMessagesService.getMessage(code, args));
    }

    @Override
    public String readStringWithPromptLocalized(String promptCode) {
        String message = localizedMessagesService.getMessage(promptCode);
        return super.readStringWithPrompt(message);
    }

    @Override
    public List<Integer> readIntForRangeLocalized(int min, int max, String errorMessageCode) {
        String message = localizedMessagesService.getMessage(errorMessageCode);
        return super.readIntForRange(min, max, message);
    }

    @Override
    public List<Integer> readIntForRangeWithPromptLocalized(int min, int max, String promptCode, String errorMessageCode) {
        String msgPrompt = localizedMessagesService.getMessage(promptCode);
        String msgError = localizedMessagesService.getMessage(errorMessageCode);
        return readIntForRangeWithPrompt(min, max, msgPrompt, msgError);
    }
}
