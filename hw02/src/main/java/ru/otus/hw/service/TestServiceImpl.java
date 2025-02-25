package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.service.io.IOService;
import ru.otus.hw.utils.formatters.OutputStreamFormatter;
import ru.otus.hw.utils.validators.AnswerValidator;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private static final String FREE_ANSWER_PROMPT = "Enter answer in a free form \\> ";
    private static final String OPTION_IDX_ANSWER_PROMPT = "Enter index (integer number) of correct answers you choose, " +
                                                           "separated by commas of whitespaces " +
                                                           "(in case of multi-variant answer) \\> ";
    private static final String USER_INVITE_PROMPT = "Please answer the questions below%n";

    private static final String ERROR_MSG_TEMPLATE = "\nYou have entered incorrect content: %s.\nTry again...\n";

    private final IOService ioService;

    private final QuestionDao questionDao;

    private final OutputStreamFormatter outputStreamFormatter;

    private String captureUserInput(Question question, IOService ioService){
        if (AnswerValidator.checkForUserFreeOption(question)) {
            return ioService.readStringWithPrompt(FREE_ANSWER_PROMPT);
        } else {
            return ioService.readStringWithPrompt(OPTION_IDX_ANSWER_PROMPT);
        }
    }

    @SneakyThrows
    private boolean processUserInput(Question question, IOService ioService){
        while (true){
            try {
                String userInput = this.captureUserInput(question, this.ioService);
                return AnswerValidator.checkAnswer(question, userInput);
            }catch (IncorrectAnswerException ex){
                String msg = String.format(ERROR_MSG_TEMPLATE, ex.getMessage());
                ioService.printError(msg);
                Thread.sleep(500);
            }
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
            var isAnswerValid = this.processUserInput(question, ioService);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }
}
