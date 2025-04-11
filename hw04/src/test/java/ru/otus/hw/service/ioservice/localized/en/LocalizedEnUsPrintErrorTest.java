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
import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.service.ioservice.config.LocalizedIoStubsConfig;
import ru.otus.hw.service.ioservice.stub.FakeStdErr;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest (classes = LocalizedIoStubsConfig.class)
@DisplayName("Check basic localized (en-US) error output behaviour")
@TestPropertySource(properties = {"test.locale=en-US"})
@ActiveProfiles(profiles = {"localized"})
@FieldDefaults(level = AccessLevel.PRIVATE)
class LocalizedEnUsPrintErrorTest extends ConfigurableByPropertiesTestBase {
    static final String HW_TEST_EXPECTED="Hello, World!" + System.lineSeparator();

    @Autowired
    @Qualifier("mockedLocalizedIO")
    LocalizedIOService localizedIoService;

    @Autowired
    FakeStdErr fakeStdErr;

    @BeforeEach
    void setUp() {
        fakeStdErr.reset();
        Locale.setDefault(Locale.US);
    }

    @DisplayName("[localized(en-US)]. printErrorLocalized (HW test)")
    @Test
    void testPlainOutput() {
        var contentKey="greeting-plain";
        localizedIoService.printErrorLocalized(contentKey);
        fakeStdErr.flush();
        assertEquals(HW_TEST_EXPECTED, fakeStdErr.getContent());
    }
}

