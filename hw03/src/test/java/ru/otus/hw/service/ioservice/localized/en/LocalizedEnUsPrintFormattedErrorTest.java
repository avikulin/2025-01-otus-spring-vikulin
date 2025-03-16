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
import ru.otus.hw.service.ioservice.stub.FakeStdErr;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Check localized (en-US) formatted error output behaviour")
@SpringBootTest(classes = {LocalizedIoStubsConfig.class})
@TestPropertySource(locations = "classpath:test-application.yml", properties = "test.locale=en-US")
@ActiveProfiles({"test","localized"})
public class LocalizedEnUsPrintFormattedErrorTest {
    static final String HW_TEST_EXPECTED="Hello, World!" + System.lineSeparator();

    @Autowired
    @Qualifier("mockedLocalizedIO")
    LocalizedIOService localizedIoService;

    @Autowired
    private FakeStdErr fakeStdErr;

    @BeforeEach
    void setUp() {
        fakeStdErr.reset();
    }


    @DisplayName("[localized(en-US)]. printFormattedErrorLocalized (HW test w/ formatting")
    @Test
    void testPlainOutput() {
        var argumentValue="World";
        var templateKey="greeting-formatted";

        localizedIoService.printFormattedErrorLocalized(templateKey, argumentValue);
        fakeStdErr.flush();
        assertEquals(HW_TEST_EXPECTED, fakeStdErr.getContent());
    }

    @DisplayName("[localized(en-US)] printFormattedError")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(LocalizedEnUsIoTestDataProvider.class)
    void testNativePrintLine(String testName, String template, Object[] args, String expectedOutput) {
        localizedIoService.printFormattedErrorLocalized(template, args);
        fakeStdErr.flush();
        var normalizedExpected = LocalizedEnUsIoTestDataProvider.normalizeNewLinePatter(expectedOutput);
        assertEquals(normalizedExpected + System.lineSeparator(), fakeStdErr.getContent());
    }
}
