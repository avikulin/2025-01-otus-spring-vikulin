package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.exceptions.QuestionStateException;
import ru.otus.hw.service.contracts.ResultService;
import ru.otus.hw.service.contracts.StudentService;
import ru.otus.hw.service.contracts.TestRunnerService;
import ru.otus.hw.service.contracts.TestService;
import ru.otus.hw.service.io.contracts.IOService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {
    private static final String MSG_QUESTION_READ_EXCEPTION  = "The internal error appeared during the load " +
                                                               "of the test configuration file with question";
    private static final String MSG_QUESTION_STATE_EXCEPTION = "The incorrect question found inside " +
                                                               "the test configuration file";

    private static final String MSG_UNKNOWN_ERROR = "Unknown error occurred. See the log file for details";

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final IOService ioService;

    @Override
    public void run() {
        log.info("Application started");
        try {
            var student = studentService.determineCurrentStudent();
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (QuestionReadException ex){
            var msg = String.format(MSG_QUESTION_READ_EXCEPTION+": %s", ex.getMessage());
            log.error(msg, ex);
            ioService.printError(MSG_QUESTION_READ_EXCEPTION+".");
        } catch (QuestionStateException ex){
            var msg = String.format(MSG_QUESTION_STATE_EXCEPTION+": %s", ex.getMessage());
            log.error(msg, ex);
            ioService.printError(MSG_QUESTION_STATE_EXCEPTION+".");
        } catch (Throwable ex) {
            var msg = String.format(MSG_UNKNOWN_ERROR+": %s", ex.getMessage());
            log.error(msg, ex);
            ioService.printError(MSG_UNKNOWN_ERROR+".");
        }
        log.info("Application finished");
    }
}
