package ru.otus.hw.utils.factories;

import com.opencsv.bean.ColumnPositionMappingStrategyBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStreamReader;


@RequiredArgsConstructor
public class OpenCsvFactory implements FactoryBean<com.opencsv.bean.CsvToBean<QuestionDto>>, ResourceLoaderAware {

    private static final String MSG_TEMPLATE_RESOURCE_READ_ERROR = "Unreachable classpath resource: %s";

    private ResourceLoader resourceLoader; // подключаем ресурсный контекст,
                                           // чтобы не нарушать принцип единой ответственности.

    private final TestFileNameProvider questionsResourceName;

    private final int skipRowsParam; // выносим все конфигурационные параметры в XML, ибо SOLID

    private final char columnSeparationCharacter;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
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

    @Override
    public CsvToBean<QuestionDto> getObject() throws Exception {
        Resource questionsResource = this.resourceLoader.getResource(this.questionsResourceName.getTestFileName());

        if (!questionsResource.isReadable()) {
            var errMsg = String.format(MSG_TEMPLATE_RESOURCE_READ_ERROR, this.questionsResourceName.getTestFileName());
            throw new QuestionReadException(errMsg);
        }

        // используем готовый поток, чтобы не связываться с путями в файловой системе (в jar-нике работать не будет)
        var resourceStream = new InputStreamReader(questionsResource.getInputStream());

        var csvBeamBuilder = getBuilder(resourceStream);
        return csvBeamBuilder.build();
    }

    @Override
    public Class<?> getObjectType() {
        return QuestionDto.class;
    }

    @Override
    public boolean isSingleton() {
        return true; // Больше одного экз. фабрики нам не нужно.
    }
}
