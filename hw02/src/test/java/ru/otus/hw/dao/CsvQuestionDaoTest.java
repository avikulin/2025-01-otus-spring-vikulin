package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.Application;
import ru.otus.hw.dao.dto.QuestionDto;
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
@ContextConfiguration(classes = Application.class)
class CsvQuestionDaoTest {
    @Nested
    @TestPropertySource(properties = {"test.fileName = dao-tests/empty-file.csv"})
    class EmptyCsvFileTest {
        @Test
        @DisplayName("Check <findAll> returns empty collection for an empty csv-file")
        void findAllForEmptyCsvFile() throws Exception {
            var context = new AnnotationConfigApplicationContext(Application.class);
            var openCsvObj = context.getBean(CsvToBean.class);
            var data = openCsvObj.parse();
            assertEquals(0, data.size());
        }
    }

    @Nested
    @TestPropertySource(properties = {"test.fileName = dao-tests/one-string.csv"})
    class CsvFileWithOneStringOnlyTest {
        @Test
        @DisplayName("Check <findAll> returns empty collection for csv-file with only one string")
        void findAllForCsvFileWithOneStringOnly() throws Exception {
            var context = new AnnotationConfigApplicationContext(Application.class);
            var openCsvObj = context.getBean(CsvToBean.class);
            var data = openCsvObj.parse();
            assertEquals(0, data.size());
        }
    }

    @Nested
    @TestPropertySource(properties = {"test.fileName =dao-tests/one-question.csv"})
    class CsvFileWithOneStringAndHeaderTest {
        @Test
        @DisplayName("Check <findAll> returns one item collection for one-question csv-file with header")
        void findAllForCsvFileWithOneStringAndHeader() throws Exception {
            var context = new AnnotationConfigApplicationContext(Application.class);
            CsvToBean<QuestionDto> openCsvObj = context.getBean(CsvToBean.class);

            var expectedQuestion = new Question("Question?",
                    List.of(
                            new Answer("Answer1", true),
                            new Answer("Answer2", false),
                            new Answer("Answer3", false)
                    )
            );
            assertNotNull(openCsvObj);

            var data = openCsvObj.parse().stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();

            assertEquals(data.size(), 1);
            var testedQuestion = data.get(0);
            assertEquals(expectedQuestion, testedQuestion);
        }
    }

    @Nested
    @TestPropertySource(properties = {"test.fileName =dao-tests/one-question.csv"})
    class CsvFileWithTwoStringsAndHeaderTest {
        @Test
        @DisplayName("Check <findAll> returns two items collection for two-questions csv-file with header")
        void findAllForCsvFileWithTwoStringsAndHeader() throws Exception {
            var context = new AnnotationConfigApplicationContext(Application.class);
            CsvToBean<QuestionDto> openCsvObj = context.getBean(CsvToBean.class);
            assertNotNull(openCsvObj);

            var expectedQuestions = List.of(
                    new Question("Question-A?",
                            List.of(
                                    new Answer("Answer-A-1", true),
                                    new Answer("Answer-A-2", false),
                                    new Answer("Answer-A-3", false)
                            )
                    ),
                    new Question("Question-B?",
                            List.of(
                                    new Answer("Answer-B-1", true),
                                    new Answer("Answer-B-2", false),
                                    new Answer("Answer-B-3", false)
                            )
                    )
            );

            var data = openCsvObj.parse().stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();

            assertEquals(data.size(), 2);
            assertEquals(expectedQuestions, data);
        }
    }
}