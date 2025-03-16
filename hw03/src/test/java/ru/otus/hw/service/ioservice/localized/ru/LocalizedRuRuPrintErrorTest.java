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
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.service.ioservice.config.LocalizedIoStubsConfig;
import ru.otus.hw.service.ioservice.stub.FakeStdErr;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest (classes = LocalizedIoStubsConfig.class)
@TestPropertySource(locations = {"classpath:test-application.yml"}, properties = "test.locale=ru_RU")
@DisplayName("Check basic localized (ru-RU) error output behaviour")
@ActiveProfiles(profiles = {"test", "localized"})
@FieldDefaults(level = AccessLevel.PRIVATE)
class LocalizedRuRuPrintErrorTest {
    static final String HW_TEST_EXPECTED="Вечер в хату!" + System.lineSeparator();

    @Autowired
    @Qualifier("mockedLocalizedIO")
    LocalizedIOService localizedIoService;

    @Autowired
    FakeStdErr fakeStdErr;

    @BeforeEach
    void setUp() {
        fakeStdErr.reset();
    }

    @DisplayName("[localized(ru-RU)]. printErrorLocalized (HW test)")
    @Test
    void testPlainOutput() {
        var contentKey="greeting-plain";
        localizedIoService.printErrorLocalized(contentKey);
        fakeStdErr.flush();
        assertEquals(HW_TEST_EXPECTED, fakeStdErr.getContent());
    }
}