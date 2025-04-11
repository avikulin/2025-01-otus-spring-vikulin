package ru.otus.hw.service.io;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.contracts.TestConfig;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.utils.formatters.base.contracts.InputFormatter;
import ru.otus.hw.utils.validators.base.contracts.InputValidator;

import java.io.InputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

@Slf4j
@Service
@Profile("native")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StreamsIOService implements IOService {
    static String MSG_PATTERN_NEWLINE = "%n";

    static String ERROR_MSG_INCORRECT_CONTENT = "You have entered incorrect content. " +
                                                "See the previous lines for details.";

    static String ERROR_MSG_TRY_AGAIN = "Try again...";

    static String ERROR_MSG_UNEXPECTED_EXCEPTION = "Unexpected exception occurred. See the previous lines for details.";

    /**
     * Приходится городить огород, чтобы не размывать границы ответственности: этот сервис работает только
     * на английском, а локализованный сервис работает, как скажут в настройках.
     * Поэтому придется позволить переопределить в <LocalizedIOService> дефолтный шаблон ошибки,
     * который выводится при неудачном вводе.
     */
    String inputErrorMsgTemplate;

    String unexpectedErrorMsg;

    InputFormatter formatter;

    InputValidator inputValidator;

    TestConfig testConfig;

    PrintStream printStream;

    PrintStream errorStream;

    Scanner scanner;

    Pattern newLinePattern;


    /**
     * Служебный закрытый конструктор, который используется для простановки
     * служебного поля <inputErrorMsgTemplate<
     * @param printStream               Ссылка на поток вывода консоли (stdout или заменитель)
     * @param errorStream               Ссылка на поток вывода ошибок (stderr или заменитель)
     * @param inputStream               Ссылка на поток ввода (stdin или заменитель)
     * @param formatter                 Ссылка на бин парсера формата ответов
     * @param inputValidator            Ссылка на валидатор вводимого значения
     * @param testConfig         Ссылка на бин конфигурации приложения
     * @param errMsgIncorrectContent    Шаблон сообщения о сути ошибки ввода
     * @param errMsgTryAgain            Шаблон сообщения о необходимости повторного ввода
     */
    protected StreamsIOService(PrintStream printStream,
                               PrintStream errorStream,
                               InputStream inputStream,
                               InputFormatter formatter,
                               InputValidator inputValidator,
                               TestConfig testConfig,
                               String errMsgIncorrectContent,
                               String errMsgTryAgain,
                               String errMsgUnexpectedException) {
        Objects.requireNonNull(printStream);
        Objects.requireNonNull(errorStream);
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(formatter);
        Objects.requireNonNull(inputValidator);
        Objects.requireNonNull(testConfig);
        Validate.notBlank(errMsgIncorrectContent);
        Validate.notBlank(errMsgTryAgain);

        this.formatter = formatter;
        this.inputValidator = inputValidator;
        this.testConfig = testConfig;
        this.printStream = printStream;
        this.errorStream = errorStream;
        this.scanner = new Scanner(inputStream);
        this.inputErrorMsgTemplate = System.lineSeparator() + errMsgIncorrectContent + "." +
                                     System.lineSeparator() + errMsgTryAgain + System.lineSeparator();
        this.unexpectedErrorMsg = errMsgUnexpectedException;
        this.newLinePattern = Pattern.compile(MSG_PATTERN_NEWLINE);
    }

    /**
     * Основной конструктор для публичного использования
     */
    @Autowired
    public StreamsIOService(@Value("#{T(System).out}") PrintStream printStream,
                            @Value("#{T(System).err}") PrintStream errorStream,
                            @Value("#{T(System).in}") InputStream inputStream,
                            InputFormatter formatter,
                            InputValidator inputValidator,
                            TestConfig testConfig) {
        this(printStream, errorStream, inputStream,
             formatter, inputValidator, testConfig,
             ERROR_MSG_INCORRECT_CONTENT, ERROR_MSG_TRY_AGAIN, ERROR_MSG_UNEXPECTED_EXCEPTION);
    }

    private String normalizeNewLinePatter(String source) {
        return newLinePattern.matcher(source).replaceAll(System.lineSeparator());
    }

    @Override
    public void print(String s) {
        var template = normalizeNewLinePatter(s);
        this.printStream.print(template);
    }

    @Override
    public void printLine(String s) {
        var template = normalizeNewLinePatter(s);
        printStream.println(template);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        var template = normalizeNewLinePatter(s + MSG_PATTERN_NEWLINE);
        var msg = MessageFormat.format(template, args);
        this.printStream.printf(msg);
    }

    @Override
    public void printError(String err) {
        var template = normalizeNewLinePatter(err);
        this.errorStream.println(template);
    }

    @Override
    public void printFormattedError(String s, Object... args) {
        var template = normalizeNewLinePatter(s + MSG_PATTERN_NEWLINE);
        var msg = MessageFormat.format(template, args);
        this.errorStream.printf(msg);
    }

    @Override
    public void printEmptyLine() {
        printStream.println();
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
                    this.print(prompt);
                }
                var rawValue = scanner.nextLine();
                var parsedValues = this.formatter.parseAnswers(rawValue);
                this.inputValidator.checkIndexValues(min, max, parsedValues);
                return parsedValues;
            } catch (NullPointerException npe) {
                reportNullPointerException(npe);
            } catch (IncorrectAnswerException ex) {
                reportIncorrectAnswer(ex);
            }
        }
        throw new IncorrectAnswerException(errorMessage);
    }

    private void reportIncorrectAnswer(IncorrectAnswerException ex) throws InterruptedException {
        // запись в лог на английском
        log.error(ERROR_MSG_INCORRECT_CONTENT);

        // локализованное сообщение для UI
        String msgLocalized = MessageFormat.format(inputErrorMsgTemplate, ex.getMessage());
        this.printFormattedError(msgLocalized);
        Thread.sleep(500); // Задержка для синхронизации stdout и stderr
        // + SneakyThrows, чтобы не городить огород.
    }

    private void reportNullPointerException(NullPointerException npe) throws InterruptedException {
        log.error("Nul-pointer exception occurred: {}", npe.getMessage());
        // запись в лог на английском
        var msgSystem = MessageFormat.format(ERROR_MSG_UNEXPECTED_EXCEPTION, npe.getMessage());
        log.error(msgSystem);

        // локализованное сообщение для UI
        this.printFormattedError(this.unexpectedErrorMsg);
        Thread.sleep(500); // Задержка для синхронизации stdout и stderr
        // + SneakyThrows, чтобы не городить огород.
    }
}
