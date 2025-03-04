package ru.otus.hw.utils.formatters.contracts;

import ru.otus.hw.domain.Question;

public interface OutputFormatter {
    void questionToStream(Question question);
}
