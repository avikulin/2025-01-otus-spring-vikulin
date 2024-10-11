package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionParseException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {

    private static final String CSV_PARSE_ERR_MSG = "CSV-resource parsing internal error";

    // создание объекта вынесено в фабрику,
    // чтобы не нарушать принцип единственной ответственности
    // и не размывать логику конфигурирования бина.
    private final CsvToBean<QuestionDto> csvToBean;

    @Override
    public List<Question> findAll() {
        try {
            return csvToBean.parse()
                    .stream()
                    .map(QuestionDto::toDomainObject)
                    .collect(Collectors.toList());
        } catch (IllegalStateException ex) {
            throw new QuestionParseException(CSV_PARSE_ERR_MSG);
        }
    }
}
