package ru.otus.hw.config;

import com.opencsv.bean.ColumnPositionMappingStrategyBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStreamReader;

@Configuration
public class CsvBeanConfig {

    private final TestFileNameProvider fileNameProvider;
    @Value("${opencsv.settings.column-separation-symbol}")
    private char columnSeparationCharacter;

    @Value("${opencsv.settings.skip-first-rows}")
    private int skipRowsParam;

    private ClassPathResource csvContent;

    private static final String MSG_TEMPLATE_RESOURCE_READ_ERROR = "Unreachable classpath resource: %s";

    @Autowired
    public CsvBeanConfig(@Qualifier("appProperties") TestFileNameProvider fileNameProvider) {
        this.fileNameProvider = fileNameProvider;
    }
    private CsvToBeanBuilder<QuestionDto> getBuilder(InputStreamReader resourceStream) {
        var beanBuilder = new CsvToBeanBuilder<QuestionDto>(resourceStream);

        // устанавливаем разделитель из XML-конфига
        beanBuilder.withSeparator(this.columnSeparationCharacter);

        // защита от пустых строк в <questions.csv?>
        beanBuilder.withFilter(tokens -> tokens != null && (tokens.length > 1 || !tokens[0].isBlank()));

        // принимаем во внимание, что в XML может быть указано 0 или -1,
        // тогда данную опцию использовать не будем.
        if (this.skipRowsParam > 0) {
            beanBuilder = beanBuilder.withSkipLines(skipRowsParam);
        }
        var strategy = new ColumnPositionMappingStrategyBuilder<QuestionDto>().build();
        strategy.setType(QuestionDto.class);
        return beanBuilder.withMappingStrategy(strategy);
    }

    @Bean
    public CsvToBean<QuestionDto> getCsvToBeanInstance() throws Exception {
        var questionsResourceName = this.fileNameProvider.getTestFileName();
        Resource questionsResource = new ClassPathResource(questionsResourceName);

        if (!questionsResource.isReadable()) {
            var errMsg = String.format(MSG_TEMPLATE_RESOURCE_READ_ERROR, questionsResourceName);
            throw new QuestionReadException(errMsg);
        }

        // используем готовый поток, чтобы не связываться с путями в файловой системе (в jar-нике работать не будет)
        var resourceStream = new InputStreamReader(questionsResource.getInputStream());

        var csvBeamBuilder = getBuilder(resourceStream);
        return csvBeamBuilder.build();
    }
}
