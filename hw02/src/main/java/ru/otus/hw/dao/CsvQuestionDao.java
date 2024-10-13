package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private static final String CSV_PARSE_ERR_MSG = "CSV-resource parsing internal error";

    private final CsvToBean<QuestionDto> csvToBean;

    @Override
    public List<Question> findAll() {
        try {
            return csvToBean.parse()
                    .stream()
                    .map(QuestionDto::toDomainObject)
                    .collect(Collectors.toList());
        } catch (IllegalStateException ex) {
            throw new QuestionReadException(CSV_PARSE_ERR_MSG);
        }
    }
}
