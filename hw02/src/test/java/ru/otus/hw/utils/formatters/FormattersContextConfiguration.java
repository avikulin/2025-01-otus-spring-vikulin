package ru.otus.hw.utils.formatters;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = {"ru.otus.hw.utils.formatters"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX,
                        pattern = "ru.otus.hw.utils.formatters.OutputStreamFormatter")
        }
)
public class FormattersContextConfiguration {}
