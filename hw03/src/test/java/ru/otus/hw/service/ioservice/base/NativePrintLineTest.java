package ru.otus.hw.service.ioservice.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import ru.otus.hw.service.ioservice.stub.FakeStdOut;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Check basic console output behaviour")
@SpringBootTest(classes = BaseIoStubsConfig.class)
@TestPropertySource("classpath:test-application.yml")
@ActiveProfiles(profiles = {"test", "native"})
class NativePrintLineTest {
    @Autowired
    @Qualifier("mockedBaseIO")
    private IOService ioService;

    @Autowired
    private FakeStdOut fakeConsole;

    @BeforeEach
    void setUp() {
        fakeConsole.reset();
    }

    @DisplayName("[native] ioService.printLine")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(NativeIoTestDataProvider.class)
    void testNativePrintLine(String testName, String template, Object[] args, String expectedOutput) {
        ioService.printLine(template);
        fakeConsole.flush();
        var normalizedExpected = NativeIoTestDataProvider.normalizeNewLinePatter(template);
        assertEquals(normalizedExpected + System.lineSeparator(), fakeConsole.getContent());
    }
}