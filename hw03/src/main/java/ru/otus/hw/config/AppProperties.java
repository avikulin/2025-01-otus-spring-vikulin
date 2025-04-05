package ru.otus.hw.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

// У меня более сложная структура файла настроек,
// поэтому сделаны вложенные классы
@Component
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppProperties {
    OpenCsvConfiguration openCsvConfiguration;

    TestServiceConfiguration testServiceConfiguration;
}
