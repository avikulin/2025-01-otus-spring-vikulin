package ru.otus.hw.service.ioservice.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.service.ioservice.config.BaseIoStubsConfig;
import ru.otus.hw.service.ioservice.provider.NativeIoTestDataProvider;
import ru.otus.hw.service.ioservice.stub.FakeStdErr;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Check formatted error output behaviour")
@SpringBootTest(classes = {BaseIoStubsConfig.class})
@TestPropertySource("classpath:test-application.yml")
@ActiveProfiles({"test","native"})
public class NativePrintFormattedErrorTest {
    @Autowired
    @Qualifier("mockedBaseIO")
    private IOService ioService;

    @Autowired
    private FakeStdErr fakeStdErr;

    @BeforeEach
    void setUp() {
        fakeStdErr.reset();
    }

    @DisplayName("[native] ioService.printFormattedError")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(NativeIoTestDataProvider.class)
    void testOutputFormatting(String testName, String template, Object[] args, String expectedOutput) {
        ioService.printFormattedError(template,args);
        fakeStdErr.flush();
        assertEquals(expectedOutput + System.lineSeparator(), fakeStdErr.getContent());
    }
}
