package ru.otus.hw.service.io;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.contracts.TestConfiguration;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.utils.formatters.contracts.InputFormatter;
import ru.otus.hw.utils.validators.contract.InputValidator;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StreamsIOService implements IOService {

    public static final String ERROR_MSG_INCORRECT_CONTENT = "You have entered incorrect content: ";
    public static final String ERROR_MSG_TRY_AGAIN = "Try again...";

    /**
     * Приходится городить огород, чтобы не размывать границы ответственности: этот сервис работает только
     * на английском, а локализованный сервис работает, как скажут в настройках.
     * Поэтому придется позволить переопределить в <LocalizedIOService> дефолтный шаблон ошибки,
     * который выводится при неудачном вводе.
     */
    String inputErrorMsgTemplate;

    InputFormatter formatter;

    InputValidator inputValidator;

    TestConfiguration testConfiguration;

    PrintStream printStream;

    PrintStream errorStream;

    Scanner scanner;


    /**
     * Служебный закрытый конструктор, который используется для простановки
     * служебного поля <inputErrorMsgTemplate<>
     * @param printStream               Ссылка на поток вывода консоли (stdout или заменитель)
     * @param errorStream               Ссылка на поток вывода ошибок (stderr или заменитель)
     * @param inputStream               Ссылка на поток ввода (stdin или заменитель)
     * @param formatter                 Ссылка на бин парсера формата ответов
     * @param inputValidator            Ссылка на валидатор вводимого значения
     * @param testConfiguration         Ссылка на бин конфигурации приложения
     * @param errMsgIncorrectContent    Шаблон сообщения о сути ошибки ввода
     * @param errMsgTryAgain            Шаблон сообщения о необходимости повторного ввода
     */
    protected StreamsIOService(PrintStream printStream,
                               PrintStream errorStream,
                               InputStream inputStream,
                               InputFormatter formatter,
                               InputValidator inputValidator,
                               TestConfiguration testConfiguration,
                               String errMsgIncorrectContent,
                               String errMsgTryAgain) {
        this.formatter = formatter;
        this.inputValidator = inputValidator;
        this.testConfiguration = testConfiguration;
        this.printStream = printStream;
        this.errorStream = errorStream;
        this.scanner = new Scanner(inputStream);
        this.inputErrorMsgTemplate = System.lineSeparator() + errMsgIncorrectContent + "%s." +
                                     System.lineSeparator() + errMsgTryAgain + System.lineSeparator();
    }

    /**
     * Основной конструктор для публичного использования
     */
    public StreamsIOService(@Value("#{T(System).out}") PrintStream printStream,
                            @Value("#{T(System).err}") PrintStream errorStream,
                            @Value("#{T(System).in}") InputStream inputStream,
                            InputFormatter formatter,
                            InputValidator inputValidator,
                            TestConfiguration testConfiguration) {
        this(printStream, errorStream, inputStream,
             formatter, inputValidator, testConfiguration,
             ERROR_MSG_INCORRECT_CONTENT, ERROR_MSG_TRY_AGAIN);
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
        var maxAttemptsLimit = this.testConfiguration.getMaxNumberOfInputDataAttempts();
        for (int i = 0; i < maxAttemptsLimit; i++) {
            try {
                if (prompt != null) {
                    this.print(prompt);
                }
                var rawValue = scanner.nextLine();
                var parsedValues = this.formatter.parseAnswers(rawValue);
                this.inputValidator.checkIndexValues(min, max, parsedValues);
                return parsedValues;
            } catch (IncorrectAnswerException ex) {
                String msg = String.format(inputErrorMsgTemplate, ex.getMessage());
                log.error(msg);
                this.printError(msg);
                Thread.sleep(500); // Задержка для синхронизации stdout и stderr
                // + SneakyThrows, чтобы не городить огород.
            }
        }
        throw new IncorrectAnswerException(errorMessage);
    }
}
