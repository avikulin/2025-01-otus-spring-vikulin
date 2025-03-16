package ru.otus.hw.dao.configuration;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import ru.otus.hw.config.CsvBeanConfig;
import ru.otus.hw.config.OpenCsvPropertiesProvider;
import ru.otus.hw.config.TestServicePropertiesProvider;
import ru.otus.hw.config.contracts.TestPropertiesProvider;
import ru.otus.hw.dao.CsvQuestionDao;

@Profile("test")
@Configuration
@EnableConfigurationProperties(OpenCsvPropertiesProvider.class)
@Import({CsvBeanConfig.class, CsvQuestionDao.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DaoContextConfiguration {}
