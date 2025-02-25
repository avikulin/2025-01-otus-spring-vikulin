package ru.otus.hw.service.ioservice;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.service.io.IOService;
import ru.otus.hw.service.ioservice.config.StubCfgInitializer;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StubCfgInitializer.class})
public class PrintFormattedLineTest {
    @Autowired
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
        ioService.printFormattedLine("%s","");
        fakeConsole.flush();
        assertEquals("\n",fakeConsole.getContent());
    }

    @Test
    @DisplayName("Char-to-char equality between input & output")
    void printRegularString(){
        ioService.printFormattedLine("%s%d%d%d%d%d%d%d%d", "AaBbCc",0,1,2,3,3,2,1,0);
        fakeConsole.flush();
        assertEquals("AaBbCc01233210"+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Non-trailing left spaces of input string")
    void printSpacedString1(){
        ioService.printFormattedLine("   %s%d%d%d%d%d%d%d%d", "AaBbCc",0,1,2,3,3,2,1,0);
        fakeConsole.flush();
        assertEquals("   AaBbCc01233210"+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Non-trailing right spaces")
    void printSpacedString2(){
        ioService.printFormattedLine("%s%d%d%d%d%d%d%d%d   ", "AaBbCc",0,1,2,3,3,2,1,0);
        fakeConsole.flush();
        assertEquals("AaBbCc01233210   "+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Keeping spaces inside input string")
    void printSpacedString3(){
        ioService.printFormattedLine("%s   %d%d%d%d%d%d%d%d", "AaBbCc",0,1,2,3,3,2,1,0);
        fakeConsole.flush();
        assertEquals("AaBbCc   01233210"+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of new-line")
    void printNonAlphabeticCharacter1(){
        ioService.printFormattedLine("%n%n%nAaBbCc01233210%n%n%n");
        fakeConsole.flush();
        var threeLineBreaks = System.lineSeparator().repeat(3);
        var fiveLineBreaks = System.lineSeparator().repeat(4);
        assertEquals(threeLineBreaks+"AaBbCc01233210"+fiveLineBreaks, fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of new-line between other letters")
    void printNonAlphabeticCharacter2(){
        ioService.printFormattedLine("AaBbCc%n%n%n01233210");
        fakeConsole.flush();
        var threeLineBreaks = System.lineSeparator().repeat(3);
        assertEquals("AaBbCc"+threeLineBreaks+"01233210"+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of non-letter & non-digit symbols")
    void printNonAlphabeticCharacter5(){
        ioService.printFormattedLine("%s","()<>/.,?!#@%^&*{}[]|");
        fakeConsole.flush();
        assertEquals("()<>/.,?!#@%^&*{}[]|"+System.lineSeparator(), fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output on unicode letters")
    void printUnicodeString(){
        ioService.printFormattedLine("%s","← → ↔ ↑ ↓ ↕ ↖ ↗ ↘ ↙ ⤡ ⤢");
        fakeConsole.flush();
        assertEquals("← → ↔ ↑ ↓ ↕ ↖ ↗ ↘ ↙ ⤡ ⤢"+System.lineSeparator(), fakeConsole.getContent());
    }
}
