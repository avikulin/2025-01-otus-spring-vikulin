package ru.otus.hw.dao.contracts;

import ru.otus.hw.domain.Question;

import java.util.List;

public interface QuestionDao {
    List<Question> findAll();
}
