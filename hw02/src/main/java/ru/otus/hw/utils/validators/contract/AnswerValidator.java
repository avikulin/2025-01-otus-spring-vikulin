package ru.otus.hw.utils.validators.contract;

import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.IncorrectAnswerException;

import java.util.List;

public interface AnswerValidator {
    boolean checkAnswer(Question question, List<Integer> answer) throws IncorrectAnswerException;
}
