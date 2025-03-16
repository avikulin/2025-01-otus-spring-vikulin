package ru.otus.hw.service.ioservice.localized.ru;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.service.ioservice.config.LocalizedIoStubsConfig;
import ru.otus.hw.service.ioservice.stub.FakeStdErr;
import ru.otus.hw.service.ioservice.stub.FakeStdIn;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest (classes = LocalizedIoStubsConfig.class)
@DisplayName("Check basic localized (ru-RU) console input behaviour")
@TestPropertySource(locations = {"classpath:test-application.yml"}, properties = "test.locale=ru-RU")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = {"test", "localized"})
@FieldDefaults(level = AccessLevel.PRIVATE)
class LocalizedRuRuInputValueTest {
    public static final String PROMPT_VALUE = "Главный вопрос жизни, Вселенной и всего такого";
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
        var promptValue = "Главный вопрос жизни, Вселенной и всего такого";
        var promptCode = "the-ultimate-question-prompt";
        var value = "42";
        this.fakeStdIn.writeContent(value + System.lineSeparator());
        var result = this.localizedIoService.readStringWithPromptLocalized(promptCode);
        this.fakeStdOut.flush();
        var consoleState = this.fakeStdOut.getContent();
        assertEquals(value, result);
        assertEquals(promptValue, consoleState);
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