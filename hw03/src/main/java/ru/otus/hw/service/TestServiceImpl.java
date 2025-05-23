package ru.otus.hw.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.contracts.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.service.contracts.TestService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.utils.formatters.localized.contracts.LocalizedOutputFormatter;
import ru.otus.hw.utils.validators.base.contracts.QuestionValidator;
import ru.otus.hw.utils.validators.localized.contracts.LocalizedAnswerValidator;

@Slf4j
@Service
@Profile("localized")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestServiceImpl implements TestService {
    static String MSG_CODE_FREE_ANSWER_PROMPT = "test-service.msg.prompt.free-answer";

    static String MSG_CODE_OPTION_IDX_ANSWER_PROMPT = "test-service.msg.prompt.fixed-index-answer";

    static String MSG_CODE_USER_INVITE_PROMPT = "test-service.msg.prompt.user-invite";

    static String MSG_CODE_CANT_OBTAIN_THE_ANSWER_ERROR = "test-service.error.cant-obtain-answer";

    static String MSG_CODE_UNEXPECTED_EXCEPTION = "test-runner-service.error.unknown";

    QuestionDao questionDao;

    LocalizedIOService localizedIoService;

    LocalizedOutputFormatter localizedOutputFormatter;

    LocalizedAnswerValidator localizedAnswerValidator;

    QuestionValidator questionValidator;

    private boolean getAndCheckUserAnswer(Question question, LocalizedIOService localizedIoService) {
        if (questionValidator.checkForUserFreeOption(question)) {
            localizedIoService.readStringWithPromptLocalized(MSG_CODE_FREE_ANSWER_PROMPT);
            return true; // ответы в свободной форме не проверяются валидатором
        } else {
            var upperIdx = question.answers().size();
            var result = false;
            try {
                var answer = localizedIoService.readIntForRangeWithPromptLocalized(1, upperIdx,
                        MSG_CODE_OPTION_IDX_ANSWER_PROMPT,
                        MSG_CODE_CANT_OBTAIN_THE_ANSWER_ERROR
                );
                result = localizedAnswerValidator.checkAnswer(question, answer);
            } catch (NullPointerException npe) {
                log.error("Nul-pointer exception occurred. See the previous lines for details");
                this.localizedIoService.printError(MSG_CODE_UNEXPECTED_EXCEPTION);
            } catch (IncorrectAnswerException e) {
                log.error("Incorrect answer occurred. See the previous lines for details");
                this.localizedIoService.printError(e.getMessage());
            }
            return result;
        }
    }

    @Override
    public TestResult executeTestFor(Student student) {
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);
        localizedIoService.printLineLocalized(MSG_CODE_USER_INVITE_PROMPT);
        for (var question: questions) {
            localizedOutputFormatter.questionToStream(question);
            var isAnswerValid = this.getAndCheckUserAnswer(question, localizedIoService);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }
}
