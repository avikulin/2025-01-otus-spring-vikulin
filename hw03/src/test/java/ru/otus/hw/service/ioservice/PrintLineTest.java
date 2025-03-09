package ru.otus.hw.service.ioservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.service.ioservice.config.IoStubsContextConfiguration;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Basic console output behaviour check")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = IoStubsContextConfiguration.class)
@TestPropertySource("classpath:/test-application.yml")
class PrintLineTest {
    @Autowired
    @Qualifier("mockedIO")
    private IOService ioService;

    @Autowired
    private FakeStdOut fakeConsole;

    @BeforeEach
    void setUp() {
        fakeConsole.reset();
    }

    @Test
    @DisplayName("No extra characters in output")
    void printEmptyLine() {
        ioService.printLine("");
        fakeConsole.flush();
        assertEquals(System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Char-to-char equality between input & output")
    void printRegularString(){
        ioService.printLine("AaBbCc01233210");
        fakeConsole.flush();
        assertEquals("AaBbCc01233210"+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Non-trailing left spaces of input string")
    void printSpacedString1(){
        ioService.printLine("   AaBbCc01233210");
        fakeConsole.flush();
        assertEquals("   AaBbCc01233210"+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Non-trailing right spaces")
    void printSpacedString2(){
        ioService.printLine("AaBbCc01233210   ");
        fakeConsole.flush();
        assertEquals("AaBbCc01233210   "+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Keeping spaces inside input string")
    void printSpacedString3(){
        ioService.printLine("AaBbCc   01233210");
        fakeConsole.flush();
        assertEquals("AaBbCc   01233210"+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of new-line")
    void printNonAlphabeticCharacter1(){
        var tripleLineFeed = System.lineSeparator().repeat(3);
        var testData = tripleLineFeed+"AaBbCc01233210"+tripleLineFeed;
        ioService.printLine(testData);
        fakeConsole.flush();
        assertEquals(testData+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of new-line between other letters")
    void printNonAlphabeticCharacter2(){
        var tripleLineFeed = System.lineSeparator().repeat(3);
        var testData = "AaBbCc"+tripleLineFeed+"01233210";
        ioService.printLine(testData);
        fakeConsole.flush();
        assertEquals(testData+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of tabulation")
    void printNonAlphabeticCharacter3(){
        ioService.printLine("\tAaBbCc01233210\t");
        fakeConsole.flush();
        assertEquals("\tAaBbCc01233210\t"+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of tabulation between other letters")
    void printNonAlphabeticCharacter4(){
        ioService.printLine("AaBbCc\t\t\t01233210");
        fakeConsole.flush();
        assertEquals("AaBbCc\t\t\t01233210"+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of non-letter & non-digit symbols")
    void printNonAlphabeticCharacter5(){
        ioService.printLine("()<>/.,?!#@%^&*{}[]|");
        fakeConsole.flush();
        assertEquals("()<>/.,?!#@%^&*{}[]|"+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output on unicode letters")
    void printUnicodeString(){
        ioService.printLine("← → ↔ ↑ ↓ ↕ ↖ ↗ ↘ ↙ ⤡ ⤢");
        fakeConsole.flush();
        assertEquals("← → ↔ ↑ ↓ ↕ ↖ ↗ ↘ ↙ ⤡ ⤢"+System.lineSeparator(), fakeConsole.getContent());
    }
}