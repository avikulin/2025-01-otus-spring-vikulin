package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.dao.configuration.DaoContextConfiguration;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Интеграционный тест с полным подъемом контекста из конфигурации.
 * Выполняется без моков, так как проверяется корректная иньекция ресурса через контекст
 */
@DisplayName("Parsing CSV-file with two questions")
@SpringBootTest(classes = DaoContextConfiguration.class)
@TestPropertySource(locations = "classpath:/test-application.yml",
                    properties = {"opencsv.settings.test-file-name=dao-tests/two-questions.csv"})
@ActiveProfiles("test")
class TwoQuestionsCsvFileDaoTest {
    @Autowired
    CsvQuestionDao dataService;

    @Test
    @DisplayName("Check <findAll> returns two items collection for two-questions csv-file with header")
    void findAllForCsvFileWithTwoStringsAndHeader() {
        assertNotNull(dataService);
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

        var data = dataService.findAll();
        assertEquals(2, data.size());
        assertEquals(expectedQuestions, data);
    }
}