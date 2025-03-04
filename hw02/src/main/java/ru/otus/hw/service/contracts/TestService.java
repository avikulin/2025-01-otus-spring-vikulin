package ru.otus.hw.service.contracts;

import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

public interface TestService {
    TestResult executeTestFor(Student student);
}
