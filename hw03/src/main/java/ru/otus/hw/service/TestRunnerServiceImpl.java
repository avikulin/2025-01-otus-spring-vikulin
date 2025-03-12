package ru.otus.hw.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.exceptions.QuestionStateException;
import ru.otus.hw.service.contracts.ResultService;
import ru.otus.hw.service.contracts.StudentService;
import ru.otus.hw.service.contracts.TestRunnerService;
import ru.otus.hw.service.contracts.TestService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestRunnerServiceImpl implements TestRunnerService {
    static String MSG_CODE_QUESTION_READ_EXCEPTION = "test-runner-service.error.question-read";

    static String MSG_CODE_QUESTION_STATE_EXCEPTION = "test-runner-service.error.question-state";

    static String MSG_CODE_UNKNOWN_ERROR = "test-runner-service.error.unknown";

    TestService testService;

    StudentService studentService;

    ResultService resultService;

    LocalizedIOService localizedIoService;

    @Override
    public void run() {
        log.info("Examination started");
        try {
            var student = studentService.determineCurrentStudent();
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (QuestionReadException ex) {
            var msg = String.format(MSG_CODE_QUESTION_READ_EXCEPTION + ": %s", ex.getMessage());
            log.error(msg, ex);
            localizedIoService.printErrorLocalized(MSG_CODE_QUESTION_READ_EXCEPTION);
        } catch (QuestionStateException ex) {
            var msg = String.format(MSG_CODE_QUESTION_STATE_EXCEPTION + ": %s", ex.getMessage());
            log.error(msg, ex);
            localizedIoService.printErrorLocalized(MSG_CODE_QUESTION_STATE_EXCEPTION);
        } catch (Throwable ex) {
            var msg = String.format(MSG_CODE_UNKNOWN_ERROR + ": %s", ex.getMessage());
            log.error(msg, ex);
            localizedIoService.printErrorLocalized(MSG_CODE_UNKNOWN_ERROR);
        }
        log.info("Examination finished");
    }
}
