package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.contracts.TestConfiguration;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.contracts.ResultService;
import ru.otus.hw.service.io.contracts.IOService;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {
    private static final String MSG_EMPTY_STRING = "";

    private static final String MSG_RESULTS_HEADER = "[ Test results ]";

    private static final String TEMPLATE_STUDENT_INFO = "Student: %s";

    private static final String TEMPLATE_ANSWERED_QUESTIONS_COUNT = "Answered questions count: %d";

    private static final String TEMPLATE_RIGHT_ANSWERS_COUNT = "Right answers count: %d";

    private static final String MSG_CONGRATULATIONS = "Congratulations! You have passed the test!";

    private static final String MSG_TEST_FAILURE = "Sorry. You have failed the test.";

    private final TestConfiguration testConfiguration;

    private final IOService ioService;

    @Override
    public void showResult(TestResult testResult) {
        ioService.printLine(MSG_EMPTY_STRING);
        ioService.printLine(MSG_RESULTS_HEADER);
        ioService.printFormattedLine(TEMPLATE_STUDENT_INFO, testResult.getStudent().getFullName());
        ioService.printFormattedLine(TEMPLATE_ANSWERED_QUESTIONS_COUNT, testResult.getAnsweredQuestions().size());
        ioService.printFormattedLine(TEMPLATE_RIGHT_ANSWERS_COUNT, testResult.getRightAnswersCount());

        if (testResult.getRightAnswersCount() >= testConfiguration.getRightAnswersCountToPass()) {
            ioService.printLine(MSG_CONGRATULATIONS);
            return;
        }
        ioService.printLine(MSG_TEST_FAILURE);
    }
}
