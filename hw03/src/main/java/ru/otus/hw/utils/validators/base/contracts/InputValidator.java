package ru.otus.hw.utils.validators.base.contracts;

import ru.otus.hw.exceptions.IncorrectAnswerException;

import java.util.List;

public interface InputValidator {
    void checkIndexValues(int min, int max, List<Integer> answerIdx) throws IncorrectAnswerException;
}
