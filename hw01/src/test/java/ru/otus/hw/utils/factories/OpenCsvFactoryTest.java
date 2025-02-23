package ru.otus.hw.utils.factories;

import com.opencsv.bean.CsvToBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw.dao.dto.QuestionDto;

import static org.junit.jupiter.api.Assertions.*;

class OpenCsvFactoryTest {

    private static ApplicationContext context;

    @BeforeAll
    static void setup(){
        context = new ClassPathXmlApplicationContext("/dao-tests/one-string-item-with-header/one-question-test-spring-context.xml");
    }

    @Test
    @DisplayName("Check instantiating <CsvToBean<QuestionDto>> from context")
    void getObjectTest(){
        OpenCsvFactory factory = context.getBean(OpenCsvFactory.class);
        CsvToBean<QuestionDto> openCsvObj;
        try {
            openCsvObj = factory.getObject();
        } catch (Exception e) {
            throw new RuntimeException("CsvToBeanFactory throw an exception on <getObject>", e);
        }
        assertNotNull(openCsvObj);
    }

    @Test
    @DisplayName("Check factory responding for <QuestionDto.class>")
    void getObjectTypeTest(){
        OpenCsvFactory factory = context.getBean(OpenCsvFactory.class);
        var clazz = factory.getObjectType();
        assertSame(QuestionDto.class, clazz);
    }
}