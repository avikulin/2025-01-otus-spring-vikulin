package ru.otus.hw.service.ioservice;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw.service.io.IOService;
import ru.otus.hw.service.io.StreamsIOService;
import ru.otus.hw.service.ioservice.stub.FakeConsole;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@Isolated
@Execution(ExecutionMode.SAME_THREAD)
@ResourceLock(value = "FAKE_CONSOLE", mode = ResourceAccessMode.READ_WRITE)
class PrintLineTest {
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
        ioService.printLine("");
        fakeConsole.flush();
        assertEquals("\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Char-to-char equality between input & output")
    void printRegularString(){
        ioService.printLine("AaBbCc01233210");
        fakeConsole.flush();
        assertEquals("AaBbCc01233210\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Non-trailing left spaces of input string")
    void printSpacedString1(){
        ioService.printLine("   AaBbCc01233210");
        fakeConsole.flush();
        assertEquals("   AaBbCc01233210\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Non-trailing right spaces")
    void printSpacedString2(){
        ioService.printLine("AaBbCc01233210   ");
        fakeConsole.flush();
        assertEquals("AaBbCc01233210   \n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Keeping spaces inside input string")
    void printSpacedString3(){
        ioService.printLine("AaBbCc   01233210");
        fakeConsole.flush();
        assertEquals("AaBbCc   01233210\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of new-line")
    void printNonAlphabeticCharacter1(){
        ioService.printLine("\n\n\nAaBbCc01233210\n\n\n");
        fakeConsole.flush();
        assertEquals("\n\n\nAaBbCc01233210\n\n\n\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of new-line between other letters")
    void printNonAlphabeticCharacter2(){
        ioService.printLine("AaBbCc\n\n\n01233210");
        fakeConsole.flush();
        assertEquals("AaBbCc\n\n\n01233210\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of tabulation")
    void printNonAlphabeticCharacter3(){
        ioService.printLine("\tAaBbCc01233210\t");
        fakeConsole.flush();
        assertEquals("\tAaBbCc01233210\t\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of tabulation between other letters")
    void printNonAlphabeticCharacter4(){
        ioService.printLine("AaBbCc\t\t\t01233210");
        fakeConsole.flush();
        assertEquals("AaBbCc\t\t\t01233210\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output of non-letter & non-digit symbols")
    void printNonAlphabeticCharacter5(){
        ioService.printLine("()<>/.,?!#@%^&*{}[]|");
        fakeConsole.flush();
        assertEquals("()<>/.,?!#@%^&*{}[]|\n", fakeConsole.getContent());
    }

    @Test
    @DisplayName("Correct output on unicode letters")
    void printUnicodeString(){
        ioService.printLine("← → ↔ ↑ ↓ ↕ ↖ ↗ ↘ ↙ ⤡ ⤢");
        fakeConsole.flush();
        assertEquals("← → ↔ ↑ ↓ ↕ ↖ ↗ ↘ ↙ ⤡ ⤢\n", fakeConsole.getContent());
    }
}