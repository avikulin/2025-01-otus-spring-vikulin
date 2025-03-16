package ru.otus.hw.service.ioservice.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import ru.otus.hw.config.TestServicePropertiesProvider;
import ru.otus.hw.config.contracts.TestPropertiesProvider;
import ru.otus.hw.service.io.StreamsIOService;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.service.ioservice.stub.FakeStdErr;
import ru.otus.hw.service.ioservice.stub.FakeStdIn;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;
import ru.otus.hw.utils.formatters.base.DefaultInputStreamFormatter;
import ru.otus.hw.utils.formatters.base.contracts.InputFormatter;
import ru.otus.hw.utils.validators.base.DefaultInputValidatorImpl;
import ru.otus.hw.utils.validators.base.contracts.InputValidator;


@Configuration
@Import({FakeStdErr.class, FakeStdIn.class, FakeStdOut.class,
        DefaultInputStreamFormatter.class, DefaultInputValidatorImpl.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Profile({"test", "native"})
@EnableConfigurationProperties({TestServicePropertiesProvider.class})
public class BaseIoStubsConfig {
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
    TestPropertiesProvider testPropertiesProvider;

    @Bean("mockedBaseIO")
    public IOService getMockedIO(){
        return new StreamsIOService(fakeStdOut.getInstance(),
                                    fakeStdErr.getInstance(),
                                    fakeStdIn.getInstance(),
                                    inputFormatter,
                                    inputValidator,
                                    testPropertiesProvider
        );
    }
}
