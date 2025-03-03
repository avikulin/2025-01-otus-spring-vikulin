package ru.otus.hw.service.io;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.utils.formatters.InputStreamFormatter;
import ru.otus.hw.utils.validators.AnswerValidator;
import ru.otus.hw.utils.validators.InputValidator;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StreamsIOService implements IOService {
    private static final String ERROR_MSG_TEMPLATE = System.lineSeparator() +
                                                     "You have entered incorrect content: %s." +
                                                     System.lineSeparator() + "Try again..." +
                                                     System.lineSeparator();

    InputStreamFormatter formatter;
    InputValidator inputValidator;
    TestConfig testConfig;
    PrintStream printStream;
    PrintStream errorStream;
    Scanner scanner;


    public StreamsIOService(@Value("#{T(System).out}") PrintStream printStream,
                            @Value("#{T(System).err}") PrintStream errorStream,
                            @Value("#{T(System).in}") InputStream inputStream,
                            InputStreamFormatter formatter,
                            InputValidator inputValidator,
                            TestConfig testConfig) {
        this.formatter = formatter;
        this.inputValidator = inputValidator;
        this.testConfig = testConfig;
        this.printStream = printStream;
        this.errorStream = errorStream;
        this.scanner = new Scanner(inputStream);
    }

    @Override
    public void print(String s) {
        printStream.print(s);
    }

    @Override
    public void printLine(String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        printStream.printf(s + "%n", args);
    }

    @Override
    public void printError(String err) {
        this.errorStream.println(err);
    }

    @Override
    public void printEmptyLine() {
        printStream.println("");
    }

    @Override
    public String readString() {
        return scanner.nextLine();
    }

    @Override
    public String readStringWithPrompt(String prompt) {
        this.print(prompt);
        return scanner.nextLine();
    }

    @Override
    public List<Integer> readIntForRange(int min, int max, String errorMessage) {
        return readIntForRangeWithPrompt(min, max, null, errorMessage);
    }

    @Override
    @SneakyThrows
    public List<Integer> readIntForRangeWithPrompt(int min, int max, String prompt, String errorMessage) {
        var maxAttemptsLimit = this.testConfig.getMaxNumberOfInputDataAttempts();
        for (int i = 0; i < maxAttemptsLimit; i++) {
            try {
                if (prompt != null) {
                    print(prompt);
                }
                var rawValue = scanner.nextLine();
                var parsedValues = this.formatter.parseAnswers(rawValue);
                this.inputValidator.checkIndexValues(min, max, parsedValues);
                return parsedValues;
            }catch (IncorrectAnswerException ex){
                String msg = String.format(ERROR_MSG_TEMPLATE, ex.getMessage());
                log.error(msg);
                this.printError(msg);
                Thread.sleep(500); // Задержка для синхронизации stdout и stderr
                // + SneakyThrows, чтобы не городить огород.
            }
        }
        throw new IncorrectAnswerException(errorMessage);

    }
}
