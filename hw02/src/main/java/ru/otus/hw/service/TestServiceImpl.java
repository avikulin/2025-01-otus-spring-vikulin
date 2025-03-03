package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.service.io.IOService;
import ru.otus.hw.utils.formatters.OutputStreamFormatter;
import ru.otus.hw.utils.validators.AnswerValidator;
import ru.otus.hw.utils.validators.QuestionValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private static final String FREE_ANSWER_PROMPT = "Enter answer in a free form \\> ";
    private static final String OPTION_IDX_ANSWER_PROMPT = "Enter index (integer number) of correct answers you choose, " +
                                                           "separated by commas of whitespaces " +
                                                           "(in case of multi-variant answer) \\> ";
    private static final String USER_INVITE_PROMPT = "Please answer the questions below%n";
    private static final String CANT_OBTAIN_THE_ANSWER_ERROR = "Can't obtain the suitable answer from user (max. attempts exceeded)." +
                                                               System.lineSeparator();
    private final IOService ioService;

    private final QuestionDao questionDao;

    private final OutputStreamFormatter outputStreamFormatter;

    private final AnswerValidator answerValidator;

    private final QuestionValidator questionValidator;

    private boolean getAndCheckUserAnswer(Question question, IOService ioService){
        if (questionValidator.checkForUserFreeOption(question)) {
            ioService.readStringWithPrompt(FREE_ANSWER_PROMPT);
            return true; // ответы в свободной форме не проверяются валидатором
        } else {
            var upperIdx = question.answers().size();
            var result = false;
            try {
                var answer = ioService.readIntForRangeWithPrompt(1, upperIdx,
                                                                    OPTION_IDX_ANSWER_PROMPT,
                                                                    CANT_OBTAIN_THE_ANSWER_ERROR
                );
                result = answerValidator.checkAnswer(question, answer);
            } catch (IncorrectAnswerException e) {
                log.error(e.getMessage());
                this.ioService.printError(e.getMessage());
                result = false;
            }
            return result;
        }
    }

    @Override
    public TestResult executeTestFor(Student student) {
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);
        ioService.printEmptyLine();
        ioService.printFormattedLine(USER_INVITE_PROMPT);
        for (var question: questions) {
            outputStreamFormatter.questionToStream(question);
            var isAnswerValid = this.getAndCheckUserAnswer(question, ioService);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }
}
