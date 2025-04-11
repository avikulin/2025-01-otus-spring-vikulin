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
import ru.otus.hw.service.ioservice.stub.FakeStdIn;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest (classes = LocalizedIoStubsConfig.class)
@DisplayName("Check basic localized (en-US) console input behaviour")
@TestPropertySource(properties = "test.locale=en-US")
@ActiveProfiles(profiles = "localized")
@FieldDefaults(level = AccessLevel.PRIVATE)
class LocalizedEnUsInputValueTest extends ConfigurableByPropertiesTestBase {
    public static final String PROMPT_VALUE = "Answer to the Ultimate Question of Life, the Universe, and Everything";
    public static final String PROMPT_CODE = "the-ultimate-question-prompt";
    public static final String ERR_CODE = "general-io-error-message";
    public static final int VALUE = 42;
    @Autowired
    @Qualifier("mockedLocalizedIO")
    LocalizedIOService localizedIoService;

    @Autowired
    FakeStdOut fakeStdOut;

    @Autowired
    FakeStdErr fakeStdErr;

    @Autowired
    FakeStdIn fakeStdIn;


    @BeforeEach
    void setUp() {
        fakeStdOut.reset();
        fakeStdErr.reset();
        fakeStdIn.reset();
    }


    @Test
    @DisplayName("[localized] readStringWithPromptLocalized")
    void readStringWithPrompt() {
        this.fakeStdIn.writeContent(VALUE + System.lineSeparator());
        var result = this.localizedIoService.readStringWithPromptLocalized(PROMPT_CODE);
        this.fakeStdOut.flush();
        var consoleState = this.fakeStdOut.getContent();
        assertEquals(String.valueOf(VALUE), result);
        assertEquals(PROMPT_VALUE, consoleState);
    }

    @Test
    @DisplayName("[localized] readIntForRangeWithPromptLocalized")
    void readIntForRangeWithPrompt() {
        this.fakeStdIn.writeContent(VALUE + System.lineSeparator());
        var result = this.localizedIoService.readIntForRangeWithPromptLocalized(41,43, PROMPT_CODE, ERR_CODE);
        this.fakeStdOut.flush();
        var consoleState = this.fakeStdOut.getContent();
        var targetValue = result.get(0);
        assertEquals(VALUE, targetValue);
        assertEquals(PROMPT_VALUE, consoleState);
    }
}