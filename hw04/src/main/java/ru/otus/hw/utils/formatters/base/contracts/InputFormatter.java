package ru.otus.hw.utils.formatters.base.contracts;

import ru.otus.hw.exceptions.IncorrectAnswerException;

import java.util.List;

public interface InputFormatter {
    List<Integer> parseAnswers(String userInput) throws IncorrectAnswerException;
}
