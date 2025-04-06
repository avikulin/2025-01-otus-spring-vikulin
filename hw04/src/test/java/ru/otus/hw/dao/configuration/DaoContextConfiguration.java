package ru.otus.hw.dao.configuration;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.OpenCsvConfiguration;
import ru.otus.hw.config.TestServiceConfiguration;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.factory.CsvBeanFactory;

@Profile("test")
@Configuration
@EnableConfigurationProperties({OpenCsvConfiguration.class, TestServiceConfiguration.class})
@Import({CsvBeanFactory.class, CsvQuestionDao.class, AppProperties.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DaoContextConfiguration {}
