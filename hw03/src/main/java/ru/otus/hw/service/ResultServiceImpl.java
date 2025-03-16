package ru.otus.hw.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.contracts.TestPropertiesProvider;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.contracts.ResultService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;

@Service
@Profile("localized")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResultServiceImpl implements ResultService {
    static String MSG_EMPTY_STRING = "";

    static String MSG_CODE_RESULTS_HEADER = "result-service.msg.result-header";

    static String MSG_CODE_TEMPLATE_STUDENT_INFO = "result-service.msg.student-info";

    static String MSG_CODE_TEMPLATE_ANSWERED_QUESTIONS_COUNT = "result-service.msg.answered-questions-count";

    static String MSG_CODE_TEMPLATE_RIGHT_ANSWERS_COUNT = "result-service.msg.right-answers-count";

    static String MSG_CODE_MSG_CONGRATULATIONS = "result-service.msg.congratulations";

    static String MSG_CODE_MSG_TEST_FAILURE = "result-service.msg.test-failure";

    TestPropertiesProvider testPropertiesProvider;

    LocalizedIOService localizedIoService;

    @Override
    public void showResult(TestResult testResult) {
        localizedIoService.printLine(MSG_EMPTY_STRING);
        localizedIoService.printLineLocalized(MSG_CODE_RESULTS_HEADER);
        localizedIoService.printFormattedLineLocalized(MSG_CODE_TEMPLATE_STUDENT_INFO, testResult.getStudent().getFullName());
        localizedIoService.printFormattedLineLocalized(MSG_CODE_TEMPLATE_ANSWERED_QUESTIONS_COUNT, testResult.getAnsweredQuestions().size());
        localizedIoService.printFormattedLineLocalized(MSG_CODE_TEMPLATE_RIGHT_ANSWERS_COUNT, testResult.getRightAnswersCount());

        if (testResult.getRightAnswersCount() >= testPropertiesProvider.getRightAnswersCountToPass()) {
            localizedIoService.printLineLocalized(MSG_CODE_MSG_CONGRATULATIONS);
            return;
        }
        localizedIoService.printLineLocalized(MSG_CODE_MSG_TEST_FAILURE);
    }
}
