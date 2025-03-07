package ru.otus.hw.service.ioservice.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import ru.otus.hw.config.CsvBeanConfig;
import ru.otus.hw.config.contracts.TestConfig;
import ru.otus.hw.service.io.StreamsIOService;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.service.ioservice.stub.FakeStdErr;
import ru.otus.hw.service.ioservice.stub.FakeStdIn;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;
import ru.otus.hw.utils.formatters.OutputStreamFormatter;
import ru.otus.hw.utils.formatters.contracts.InputFormatter;
import ru.otus.hw.utils.validators.contract.AnswerValidator;
import ru.otus.hw.utils.validators.contract.InputValidator;
import ru.otus.hw.utils.validators.contract.QuestionValidator;


@Configuration
@ComponentScan(basePackages = {"ru.otus.hw.service.ioservice.stub",
                               "ru.otus.hw.utils.validators",
                               "ru.otus.hw.utils.formatters",
                               "ru.otus.hw.config"},
        excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                                  classes = {StreamsIOService.class,
                                             CsvBeanConfig.class,
                                             OutputStreamFormatter.class,
                                          AnswerValidator.class,
                                          QuestionValidator.class
                                  }
          )
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IoStubsContextConfiguration {
    @Autowired
    FakeStdOut fakeStdOut;

    @Autowired
    FakeStdErr fakeStdErr;

    @Autowired
    FakeStdIn fakeStdIn;

    @Autowired
    InputFormatter inputFormatter;

    @Autowired
    InputValidator inputValidator;

    @Autowired
    TestConfig testConfig;

    @Bean("mockedIO")
    public IOService getMockedIO(){
        return new StreamsIOService(fakeStdOut.getInstance(),
                                    fakeStdErr.getInstance(),
                                    fakeStdIn.getInstance(),
                inputFormatter,
                                    inputValidator,
                                    testConfig);
    }
}
