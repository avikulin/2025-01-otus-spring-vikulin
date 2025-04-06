package ru.otus.hw.service.ioservice.base;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.service.ioservice.config.BaseIoStubsConfig;
import ru.otus.hw.service.ioservice.provider.NativeIoTestDataProvider;
import ru.otus.hw.service.ioservice.stub.FakeStdErr;
import ru.otus.hw.service.ioservice.stub.FakeStdIn;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = BaseIoStubsConfig.class)
@TestPropertySource(locations = {"classpath:test-application.yml"})
@DisplayName("Check basic user input behaviour")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = {"test", "native"})
@FieldDefaults(level = AccessLevel.PRIVATE)
class NativeInputValueTest {
    @Autowired
    @Qualifier("mockedBaseIO")
    IOService ioService;

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

    @ParameterizedTest(name = "{0}")
    @DisplayName("[native] readString")
    @ArgumentsSource(NativeIoTestDataProvider.class)
    void readString(String testName, String template, Object[] args, String expectedOutput) {
        this.fakeStdIn.writeContent(template + System.lineSeparator());
        var value = this.ioService.readString();
        assertEquals(template, value);
    }

    @Test
    @DisplayName("[native] readStringWithPrompt")
    void readStringWithPrompt() {
        var prompt = "prompt";
        var value = "value";
        this.fakeStdIn.writeContent(value + System.lineSeparator());
        var result = this.ioService.readStringWithPrompt(prompt);
        this.fakeStdOut.flush();
        var consoleState = this.fakeStdOut.getContent();
        assertEquals(value, result);
        assertEquals(prompt, consoleState);
    }


    @Test
    @DisplayName("[native] readIntForRange")
    void readIntForRange() {
        var value = 42;
        this.fakeStdIn.writeContent(value + System.lineSeparator());
        var result = this.ioService.readIntForRange(41,43, "not-checked");
        var targetValue = result.get(0);
        assertEquals(value, targetValue);
    }

    @Test
    @DisplayName("[native] readStringWithPrompt")
    void readIntForRangeWithPrompt() {
        var prompt = "Answer to the Ultimate Question of Life, the Universe, and Everything";
        var value = 42;
        this.fakeStdIn.writeContent(value + System.lineSeparator());
        var result = this.ioService.readIntForRangeWithPrompt(41,43, prompt,"not-checked");
        this.fakeStdOut.flush();
        var consoleState = this.fakeStdOut.getContent();
        var targetValue = result.get(0);
        assertEquals(value, targetValue);
        assertEquals(prompt, consoleState);
    }
}