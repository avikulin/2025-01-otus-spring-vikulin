package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.utils.factories.OpenCsvFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционный тест с полным подъемом контекста из конфигурации.
 * Выполняется без моков, так как проверяется корректная иньекция ресурса через контекст
 */
class CsvQuestionDaoTest {

    @Test
    @DisplayName("Check <findAll> returns empty collection for an empty csv-file")
    void findAllForEmptyCsvFile() throws Exception {
        var context = new ClassPathXmlApplicationContext("dao-tests/empty-file/empty-file-test-spring-context.xml");
        OpenCsvFactory factory = context.getBean(OpenCsvFactory.class);

        var openCsvObj = factory.getObject();
        assertNotNull(openCsvObj);

        var data = openCsvObj.parse();
        assertEquals(0, data.size());
    }

    @Test
    @DisplayName("Check <findAll> returns one item collection for one-question csv-file with header")
    void findAllForCsvFileWithOneStringAndHeader() throws Exception {
        var context = new ClassPathXmlApplicationContext("dao-tests/one-string-item-with-header/one-question-test-spring-context.xml");
        OpenCsvFactory factory = context.getBean(OpenCsvFactory.class);

        var expectedQuestion = new Question("Question?",
                List.of(
                        new Answer("Answer1",true),
                        new Answer("Answer2",false),
                        new Answer("Answer3",false)
                )
        );
        var openCsvObj = factory.getObject();
        assertNotNull(openCsvObj);

        var data = openCsvObj.parse().stream()
                .map(QuestionDto::toDomainObject)
                .toList();

        assertEquals(data.size(),1);
        var testedQuestion = data.get(0);
        assertEquals(expectedQuestion, testedQuestion);
    }

    @Test
    @DisplayName("Check <findAll> returns empty collection for csv-file with only one string")
    void findAllForCsvFileWithOneStringOnly() throws Exception {
        var context = new ClassPathXmlApplicationContext("dao-tests/one-string-only/one-string-test-spring-context.xml");
        OpenCsvFactory factory = context.getBean(OpenCsvFactory.class);

        var openCsvObj = factory.getObject();
        assertNotNull(openCsvObj);

        var data = openCsvObj.parse();
        assertEquals(0, data.size());
    }

    @Test
    @DisplayName("Check <findAll> returns two items collection for two-questions csv-file with header")
    void findAllForCsvFileWithTwoStringsAndHeader() throws Exception {
        var context = new ClassPathXmlApplicationContext("dao-tests/two-string-items-with-header/two-questions-test-spring-context.xml");
        OpenCsvFactory factory = context.getBean(OpenCsvFactory.class);

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

        var openCsvObj = factory.getObject();
        assertNotNull(openCsvObj);

        var data = openCsvObj.parse().stream()
                .map(QuestionDto::toDomainObject)
                .toList();

        assertEquals(data.size(),2);
        assertEquals(expectedQuestions, data);
    }
}