package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Интеграционный тест с полным подъемом контекста из конфигурации.
 * Выполняется без моков, так как проверяется корректная иньекция ресурса через контекст
 */
@DisplayName("Parsing empty CSV-file")
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = DaoContextConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestPropertySource(properties = {"opencsv.settings.column-separation-symbol=;",
                                  "opencsv.settings.skip-first-rows=1",
                                  "test.right-answers-count-to-pass=3",
                                  "test.filename=dao-tests/empty-file.csv",
                                  "test.max-number-of-input-data-attempts=10"})
class EmptyCsvFileDaoTest {


    @Autowired
    CsvQuestionDao dataService;

    @Test
    @DisplayName("Check <findAll> returns empty collection for an empty csv-file")
    void findAllForEmptyCsvFile() {
        assertNotNull(dataService);
        var data = dataService.findAll();
        assertEquals(0, data.size());
    }
}