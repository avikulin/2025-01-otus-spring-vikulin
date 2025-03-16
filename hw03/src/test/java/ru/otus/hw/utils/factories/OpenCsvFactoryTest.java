package ru.otus.hw.utils.factories;

import com.opencsv.bean.CsvToBean;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.CsvBeanConfig;
import ru.otus.hw.config.contracts.TestFileReaderPropertiesProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenCsvFactoryTest {

    @Mock
    private TestFileReaderPropertiesProvider testFileReaderConfig;

    @InjectMocks
    private CsvBeanConfig factoryBean;

    @BeforeEach
    public void setupTest(){
        reset(testFileReaderConfig);
        when(testFileReaderConfig.getColumnSeparationSymbol()).thenReturn(';');
        when(testFileReaderConfig.getNumberOfRowsSkipped()).thenReturn(1);
        when(testFileReaderConfig.getTestFileName()).thenReturn("dao-tests/empty-file.csv");
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