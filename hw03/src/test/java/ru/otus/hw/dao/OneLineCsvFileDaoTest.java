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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Интеграционный тест с полным подъемом контекста из конфигурации.
 * Выполняется без моков, так как проверяется корректная иньекция ресурса через контекст
 */
@DisplayName("Parsing one line CSV-file")
@SpringBootTest(classes = DaoContextConfiguration.class)
@TestPropertySource(locations = "classpath:/test-application.yml",
                    properties = {"opencsv.settings.test-file-name=dao-tests/one-string.csv"})
@ActiveProfiles("test")
class OneLineCsvFileDaoTest {
    @Autowired
    CsvQuestionDao dataService;

    @Test
    @DisplayName("Check <findAll> returns empty collection for csv-file with only one string")
    void findAllForCsvFileWithOneStringOnly() {
        assertNotNull(dataService);
        var data = dataService.findAll();
        assertEquals(0, data.size());
    }
}