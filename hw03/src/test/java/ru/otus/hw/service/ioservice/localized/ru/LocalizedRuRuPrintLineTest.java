package ru.otus.hw.service.ioservice.localized.ru;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.service.ioservice.config.LocalizedIoStubsConfig;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Check basic localized (ru-RU) console output behaviour")
@SpringBootTest (classes = LocalizedIoStubsConfig.class)
@TestPropertySource(properties = "test.locale=ru-RU")
@ActiveProfiles(profiles = "localized")
@FieldDefaults(level = AccessLevel.PRIVATE)
class LocalizedRuRuPrintLineTest  extends ConfigurableByPropertiesTestBase {
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

    @DisplayName("[localized(ru-RU)]. printLineLocalized (HW test)")
    @Test
    void testPlainOutput() {
        var contentKey="greeting-plain";
        localizedIoService.printLineLocalized(contentKey);
        fakeConsole.flush();
        assertEquals(HW_TEST_EXPECTED, fakeConsole.getContent());
    }
}