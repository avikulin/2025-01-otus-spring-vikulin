package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.service.io.IOService;
import ru.otus.hw.utils.formatters.OutputStreamFormatter;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printEmptyLine();
        ioService.printFormattedLine("Please answer the questions below:%n");

        // Получить вопросы из дао и вывести их с вариантами ответов
        var questions = this.questionDao.findAll();
        if (!questions.isEmpty()) { // репозиторий может быть пустым
            questions.forEach(question -> {
                OutputStreamFormatter.questionToStream(question, ioService);
                ioService.printEmptyLine();
            });
        }
    }
}
