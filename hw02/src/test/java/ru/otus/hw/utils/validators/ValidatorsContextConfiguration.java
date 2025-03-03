package ru.otus.hw.utils.validators;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import ru.otus.hw.config.CsvBeanConfig;
import ru.otus.hw.utils.formatters.OutputStreamFormatter;

@Configuration
@ComponentScan(basePackages = {"ru.otus.hw.utils.formatters",
                               "ru.otus.hw.service.io",
                               "ru.otus.hw.utils.validators",
                               "ru.otus.hw.config"},
               excludeFilters = {
                    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                                          classes = {CsvBeanConfig.class, OutputStreamFormatter.class})
               })
public class ValidatorsContextConfiguration {
}
