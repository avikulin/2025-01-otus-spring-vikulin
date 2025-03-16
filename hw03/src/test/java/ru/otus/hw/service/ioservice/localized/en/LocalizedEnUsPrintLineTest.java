package ru.otus.hw.service.ioservice.localized.en;

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
import ru.otus.hw.service.ioservice.stub.FakeStdOut;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Check basic localized (en-US) console output behaviour")
@SpringBootTest (classes = LocalizedIoStubsConfig.class)
@TestPropertySource(locations = {"classpath:test-application.yml"}, properties = "test.locale=en-US")
@ActiveProfiles(profiles = {"test", "localized"})
@FieldDefaults(level = AccessLevel.PRIVATE)
class LocalizedEnUsPrintLineTest {
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

    @DisplayName("[localized(en-US)]. printLineLocalized (HW test)")
    @Test
    void testPlainOutput() {
        var contentKey="greeting-plain";
        localizedIoService.printLineLocalized(contentKey);
        fakeConsole.flush();
        assertEquals(HW_TEST_EXPECTED, fakeConsole.getContent());
    }
}