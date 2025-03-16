package ru.otus.hw.service.ioservice.localized.ru;

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
import ru.otus.hw.service.ioservice.provider.LocalizedRuRuIoTestDataProvider;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Check localized (ru-RU) formatted console output behaviour")
@SpringBootTest(classes = {LocalizedIoStubsConfig.class})
@TestPropertySource(locations = "classpath:test-application.yml", properties = "test.locale=ru_RU")
@ActiveProfiles({"test","localized"})
public class LocalizedRuRuPrintFormattedLineTest {

    static final String HW_TEST_EXPECTED="Вечер в хату!" + System.lineSeparator();

    @Autowired
    @Qualifier("mockedLocalizedIO")
    LocalizedIOService localizedIoService;

    @Autowired
    private FakeStdOut fakeConsole;

    @BeforeEach
    void setUp() {
        fakeConsole.reset();
    }


    @DisplayName("[localized(ru-RU)] printFormattedLineLocalized (HW test w/ formatting)")
    @Test
    void testPlainOutput() {
        var argumentValue="хату";
        var templateKey="greeting-formatted";

        localizedIoService.printFormattedLineLocalized(templateKey, argumentValue);
        fakeConsole.flush();
        assertEquals(HW_TEST_EXPECTED, fakeConsole.getContent());
    }

    @DisplayName("[localized(ru-RU)] ioService.printFormattedLineLocalized")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(LocalizedRuRuIoTestDataProvider.class)
    void testNativePrintLine(String testName, String template, Object[] args, String expectedOutput) {
        localizedIoService.printFormattedLineLocalized(template, args);
        fakeConsole.flush();
        var normalizedExpected = LocalizedEnUsIoTestDataProvider.normalizeNewLinePatter(expectedOutput);
        assertEquals(normalizedExpected + System.lineSeparator(), fakeConsole.getContent());
    }
}
