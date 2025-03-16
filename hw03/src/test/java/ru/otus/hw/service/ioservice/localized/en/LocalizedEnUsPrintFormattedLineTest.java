package ru.otus.hw.service.ioservice.localized.en;

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
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.service.ioservice.config.LocalizedIoStubsConfig;
import ru.otus.hw.service.ioservice.provider.LocalizedEnUsIoTestDataProvider;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Check localized (en-US) formatted console output behaviour")
@SpringBootTest(classes = {LocalizedIoStubsConfig.class})
@TestPropertySource(locations = "classpath:test-application.yml", properties = "test.locale=en-US")
@ActiveProfiles({"test","localized"})
public class LocalizedEnUsPrintFormattedLineTest {

    static final String HW_TEST_EXPECTED="Hello, World!" + System.lineSeparator();

    @Autowired
    @Qualifier("mockedLocalizedIO")
    LocalizedIOService localizedIoService;

    @Autowired
    private FakeStdOut fakeConsole;

    @BeforeEach
    void setUp() {
        fakeConsole.reset();
    }


    @DisplayName("[localized(en-US)] printFormattedLineLocalized (HW test w/ formatting)")
    @Test
    void testPlainOutput() {
        var argumentValue="World";
        var templateKey="greeting-formatted";

        localizedIoService.printFormattedLineLocalized(templateKey, argumentValue);
        fakeConsole.flush();
        assertEquals(HW_TEST_EXPECTED, fakeConsole.getContent());
    }

    @DisplayName("[localized(en-US)] ioService.printFormattedLineLocalized")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(LocalizedEnUsIoTestDataProvider.class)
    void testNativePrintLine(String testName, String template, Object[] args, String expectedOutput) {
        localizedIoService.printFormattedLineLocalized(template, args);
        fakeConsole.flush();
        var normalizedExpected = LocalizedEnUsIoTestDataProvider.normalizeNewLinePatter(expectedOutput);
        assertEquals(normalizedExpected + System.lineSeparator(), fakeConsole.getContent());
    }
}
