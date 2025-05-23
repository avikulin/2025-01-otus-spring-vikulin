package ru.otus.hw.utils.validators.base.contracts;

import ru.otus.hw.domain.Question;

public interface QuestionValidator {
    void validateQuestion(Question obj);

    boolean checkForUserFreeOption(Question question);
}
