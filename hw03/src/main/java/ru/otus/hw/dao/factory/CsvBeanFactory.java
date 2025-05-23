package ru.otus.hw.dao.factory;

import com.opencsv.bean.ColumnPositionMappingStrategyBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStreamReader;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CsvBeanFactory {
    static String MSG_TEMPLATE_RESOURCE_READ_ERROR = "Unreachable classpath resource: %s";

    static String MSG_UNDEFINED_PATH_ERROR = "The test question path is undefined for this locale.";

    AppProperties appCfg;

    private CsvToBeanBuilder<QuestionDto> getBuilder(InputStreamReader resourceStream) {
        var beanBuilder = new CsvToBeanBuilder<QuestionDto>(resourceStream);

        var openCsvConfig = this.appCfg.getOpenCsvConfiguration();
        // устанавливаем разделитель из XML-конфига
        beanBuilder.withSeparator(openCsvConfig.getColumnSeparationSymbol());

        // защита от пустых строк в <questions.csv?>
        beanBuilder.withFilter(tokens -> tokens != null && (tokens.length > 1 || !tokens[0].isBlank()));

        // принимаем во внимание, что в конфигурационном файле может быть указано 0 или -1,
        // тогда данную опцию использовать не будем.
        if (openCsvConfig.getNumberOfRowsSkipped() > 0) {
            beanBuilder = beanBuilder.withSkipLines(openCsvConfig.getNumberOfRowsSkipped());
        }
        var strategy = new ColumnPositionMappingStrategyBuilder<QuestionDto>().build();
        strategy.setType(QuestionDto.class);
        return beanBuilder.withMappingStrategy(strategy);
    }

    @Bean
    public CsvToBean<QuestionDto> getCsvToBeanInstance() throws Exception {
        var testConfig = this.appCfg.getTestServiceConfiguration();
        var questionsResourceName = testConfig.getTestFileName();
        if (questionsResourceName == null) {
            throw new QuestionReadException(MSG_UNDEFINED_PATH_ERROR);
        }

        Resource questionsResource = new ClassPathResource(questionsResourceName);

        if (!questionsResource.isReadable()) {
            var errMsg = String.format(MSG_TEMPLATE_RESOURCE_READ_ERROR, questionsResourceName);
            throw new QuestionReadException(errMsg);
        }

        // используем готовый поток, чтобы не связываться с путями в файловой системе (в jar-нике работать не будет)
        var resourceStream = new InputStreamReader(questionsResource.getInputStream());

        var csvBeamBuilder = this.getBuilder(resourceStream);
        return csvBeamBuilder.build();
    }
}
