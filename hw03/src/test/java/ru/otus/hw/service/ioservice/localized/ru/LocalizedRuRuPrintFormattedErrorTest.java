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
import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.service.ioservice.config.LocalizedIoStubsConfig;
import ru.otus.hw.service.ioservice.provider.LocalizedEnUsIoTestDataProvider;
import ru.otus.hw.service.ioservice.provider.LocalizedRuRuIoTestDataProvider;
import ru.otus.hw.service.ioservice.stub.FakeStdErr;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Check localized (ru-RU) formatted error output behaviour")
@SpringBootTest(classes = {LocalizedIoStubsConfig.class})
@TestPropertySource(properties = "test.locale=ru-RU")
@ActiveProfiles("localized")
public class LocalizedRuRuPrintFormattedErrorTest  extends ConfigurableByPropertiesTestBase {
    static final String HW_TEST_EXPECTED="Вечер в хату!" + System.lineSeparator();

    @Autowired
    @Qualifier("mockedLocalizedIO")
    LocalizedIOService localizedIoService;

    @Autowired
    private FakeStdErr fakeStdErr;

    @BeforeEach
    void setUp() {
        fakeStdErr.reset();
    }


    @DisplayName("[localized(ru-RU)]. printFormattedErrorLocalized (HW test w/ formatting")
    @Test
    void testPlainOutput() {
        var argumentValue="хату";
        var templateKey="greeting-formatted";

        localizedIoService.printFormattedErrorLocalized(templateKey, argumentValue);
        fakeStdErr.flush();
        assertEquals(HW_TEST_EXPECTED, fakeStdErr.getContent());
    }

    @DisplayName("[localized(ru-RU)] printFormattedError")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(LocalizedRuRuIoTestDataProvider.class)
    void testNativePrintLine(String testName, String template, Object[] args, String expectedOutput) {
        localizedIoService.printFormattedErrorLocalized(template, args);
        fakeStdErr.flush();
        var normalizedExpected = LocalizedEnUsIoTestDataProvider.normalizeNewLinePatter(expectedOutput);
        assertEquals(normalizedExpected + System.lineSeparator(), fakeStdErr.getContent());
    }
}
