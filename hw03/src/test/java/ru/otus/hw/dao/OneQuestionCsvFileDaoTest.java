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
@DisplayName("Parsing CSV-file with one question")
@SpringBootTest(classes = DaoContextConfiguration.class)
@TestPropertySource(locations = "classpath:/test-application.yml",
                    properties = {"opencsv.settings.test-file-name=dao-tests/one-question.csv"})
@ActiveProfiles("test")
class OneQuestionCsvFileDaoTest {
    @Autowired
    CsvQuestionDao dataService;

    @Test
    @DisplayName("Check <findAll> returns one item collection for one-question csv-file with header")
    void findAllForCsvFileWithOneStringAndHeader() {
        assertNotNull(dataService);
        var expectedQuestion = new Question("Question?",
                List.of(
                        new Answer("Answer1", true),
                        new Answer("Answer2", false),
                        new Answer("Answer3", false)
                )
        );
        var data = dataService.findAll();
        assertEquals(1, data.size());
        var testedQuestion = data.get(0);
        assertEquals(expectedQuestion, testedQuestion);
    }
}