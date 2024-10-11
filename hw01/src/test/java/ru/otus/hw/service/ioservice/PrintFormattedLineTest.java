package ru.otus.hw.service.ioservice;

import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw.service.io.IOService;
import ru.otus.hw.service.ioservice.stub.FakeConsole;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrintFormattedLineTest {
    private static IOService ioService;
    private static FakeConsole fakeConsole;

    @BeforeAll
    public static void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("io-tests/io-test-spring-context.xml");
        ioService = context.getBean(IOService.class);
        fakeConsole = context.getBean(FakeConsole.class);
    }

    @BeforeEach
    void setUp() {
        fakeConsole.reset();
    }


    @AfterAll
    static void tearDown() {
        fakeConsole.close();
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
        assertEquals("AaBbCc01233210\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Non-trailing left spaces of input string")
    void printSpacedString1(){
        ioService.printFormattedLine("   %s%d%d%d%d%d%d%d%d", "AaBbCc",0,1,2,3,3,2,1,0);
        fakeConsole.flush();
        assertEquals("   AaBbCc01233210\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Non-trailing right spaces")
    void printSpacedString2(){
        ioService.printFormattedLine("%s%d%d%d%d%d%d%d%d   ", "AaBbCc",0,1,2,3,3,2,1,0);
        fakeConsole.flush();
        assertEquals("AaBbCc01233210   \n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Keeping spaces inside input string")
    void printSpacedString3(){
        ioService.printFormattedLine("%s   %d%d%d%d%d%d%d%d", "AaBbCc",0,1,2,3,3,2,1,0);
        fakeConsole.flush();
        assertEquals("AaBbCc   01233210\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of new-line")
    void printNonAlphabeticCharacter1(){
        ioService.printFormattedLine("%n%n%nAaBbCc01233210%n%n%n");
        fakeConsole.flush();
        assertEquals("\n\n\nAaBbCc01233210\n\n\n\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of new-line between other letters")
    void printNonAlphabeticCharacter2(){
        ioService.printFormattedLine("AaBbCc%n%n%n01233210");
        fakeConsole.flush();
        assertEquals("AaBbCc\n\n\n01233210\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of non-letter & non-digit symbols")
    void printNonAlphabeticCharacter5(){
        ioService.printFormattedLine("%s","()<>/.,?!#@%^&*{}[]|");
        fakeConsole.flush();
        assertEquals("()<>/.,?!#@%^&*{}[]|\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output on unicode letters")
    void printUnicodeString(){
        ioService.printFormattedLine("%s","← → ↔ ↑ ↓ ↕ ↖ ↗ ↘ ↙ ⤡ ⤢");
        fakeConsole.flush();
        assertEquals("← → ↔ ↑ ↓ ↕ ↖ ↗ ↘ ↙ ⤡ ⤢\n", fakeConsole.getContent());
    }
}
