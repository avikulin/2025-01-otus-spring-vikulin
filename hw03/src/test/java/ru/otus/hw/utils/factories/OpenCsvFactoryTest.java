package ru.otus.hw.utils.factories;

import com.opencsv.bean.CsvToBean;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.OpenCsvConfiguration;
import ru.otus.hw.config.TestServiceConfiguration;
import ru.otus.hw.dao.factory.CsvBeanFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = OpenCsvFactoryTest.TestConfig.class)
@Import({CsvBeanFactory.class, AppProperties.class})
@TestPropertySource(properties = {"test.locale=en-US",
                                  "test.file-name-by-locale-tag.en-US=/dao-tests/empty-file.csv"})
class OpenCsvFactoryTest extends ConfigurableByPropertiesTestBase {
    @Autowired
    private CsvBeanFactory factoryBean;

    @Configuration
    @EnableConfigurationProperties({OpenCsvConfiguration.class, TestServiceConfiguration.class})
    static class TestConfig{
        @Autowired
        AppProperties appProperties;

        public AppProperties construct(){
            var mockedProps = Mockito.mock(new AppProperties(this.appProperties.getOpenCsvConfiguration(),
                                                             this.appProperties.getTestServiceConfiguration())
            );
            when(mockedProps.getTestServiceConfiguration().getTestFileName()).thenReturn("dao-tests/empty-file.csv");
            return mockedProps;
        }
    }

    @Test
    @DisplayName("Check instantiating <CsvToBean<QuestionDto>> from context")
    @SneakyThrows
    void getObjectTest(){
        var bean = factoryBean.getCsvToBeanInstance();
        assertNotNull(bean);
        assertEquals(CsvToBean.class, bean.getClass());
    }
}