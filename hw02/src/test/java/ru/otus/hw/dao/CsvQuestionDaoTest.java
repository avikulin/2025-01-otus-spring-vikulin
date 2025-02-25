package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.config.CsvBeanConfig;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Интеграционный тест с полным подъемом контекста из конфигурации.
 * Выполняется без моков, так как проверяется корректная иньекция ресурса через контекст
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CsvBeanConfig.class})
class CsvQuestionDaoTest {
    CsvQuestionDao dataService;

    @Test
    @DisplayName("Check <findAll> returns empty collection for an empty csv-file")
    void findAllForEmptyCsvFile() {
        var context = new ClassPathXmlApplicationContext("dao-tests/empty-file/empty-file-test-spring-context.xml");
        assertNotNull(dataService);
        var data = dataService.findAll();
        assertEquals(0, data.size());
    }

    @Test
    @DisplayName("Check <findAll> returns one item collection for one-question csv-file with header")
    void findAllForCsvFileWithOneStringAndHeader() {
        var context = new ClassPathXmlApplicationContext("dao-tests/one-string-item-with-header/one-question-test-spring-context.xml");
        var dataService = context.getBean(CsvQuestionDao.class);
        assertNotNull(dataService);
        var expectedQuestion = new Question("Question?",
                List.of(
                        new Answer("Answer1",true),
                        new Answer("Answer2",false),
                        new Answer("Answer3",false)
                )
        );
        var data = dataService.findAll();
        assertEquals(1,data.size());
        var testedQuestion = data.get(0);
        assertEquals(expectedQuestion, testedQuestion);
    }

    @Test
    @DisplayName("Check <findAll> returns empty collection for csv-file with only one string")
    void findAllForCsvFileWithOneStringOnly() {
        var context = new ClassPathXmlApplicationContext("dao-tests/one-string-only/one-string-test-spring-context.xml");
        var dataService = context.getBean(CsvQuestionDao.class);
        assertNotNull(dataService);
        var data = dataService.findAll();
        assertEquals(0, data.size());
    }

    @Test
    @DisplayName("Check <findAll> returns two items collection for two-questions csv-file with header")
    void findAllForCsvFileWithTwoStringsAndHeader() {
        var context = new ClassPathXmlApplicationContext("dao-tests/two-string-items-with-header/two-questions-test-spring-context.xml");
        var dataService = context.getBean(CsvQuestionDao.class);
        assertNotNull(dataService);

        var expectedQuestions = List.of(
                new Question("Question-A?",
                                   List.of(
                                            new Answer("Answer-A-1",true),
                                            new Answer("Answer-A-2",false),
                                            new Answer("Answer-A-3",false)
                                   )
                ),
                new Question("Question-B?",
                        List.of(
                                new Answer("Answer-B-1",true),
                                new Answer("Answer-B-2",false),
                                new Answer("Answer-B-3",false)
                        )
                )
        );

        var data = dataService.findAll();
        assertEquals(2,data.size());
        assertEquals(expectedQuestions, data);
    }
}