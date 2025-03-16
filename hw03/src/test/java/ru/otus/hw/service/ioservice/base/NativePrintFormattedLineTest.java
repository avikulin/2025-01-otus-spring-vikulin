package ru.otus.hw.service.ioservice.base;

import jdk.jfr.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.service.ioservice.config.BaseIoStubsConfig;
import ru.otus.hw.service.ioservice.provider.NativeIoTestDataProvider;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Check formatted console output behaviour")
@SpringBootTest(classes = {BaseIoStubsConfig.class})
@TestPropertySource("classpath:test-application.yml")
@ActiveProfiles({"test","native"})
public class NativePrintFormattedLineTest {
    @Autowired
    @Qualifier("mockedBaseIO")
    private IOService ioService;

    @Autowired
    private FakeStdOut fakeConsole;

    @BeforeEach
    void setUp() {
        fakeConsole.reset();
    }


    @DisplayName("[native] ioService.printFormattedLine")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(NativeIoTestDataProvider.class)
    void testOutputFormatting(String testName, String template, Object[] args, String expectedOutput) {
        ioService.printFormattedLine(template,args);
        fakeConsole.flush();
        assertEquals(expectedOutput + System.lineSeparator(),fakeConsole.getContent());
    }
}
