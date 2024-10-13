package ru.otus.hw.service.ioservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.service.io.IOService;
import ru.otus.hw.service.ioservice.stub.FakeStdErr;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;
import ru.otus.hw.service.ioservice.config.StubCfgInitializer;
import ru.otus.hw.service.ioservice.utils.Normalizer;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {StubCfgInitializer.class})
public class PrintFormattedLineTest {
    @Qualifier("getMockedIO")
    @Autowired
    private IOService ioService;
    @Autowired
    private FakeStdOut fakeStdOut;
    @Autowired
    private FakeStdErr fakeStdErr;

    @BeforeEach
    public void setupMocks(){
        this.fakeStdOut.reset();
        this.fakeStdErr.reset();
    }

    private static final Stream<Arguments> testData = Stream.of(
            Arguments.of("No extra characters in output","%s",new Object[]{""},"\n"),
            Arguments.of("Char-to-char equality between input & output",
                    "%s%d%d%d%d%d%d%d%d",
                    new Object[]{"AaBbCc",0,1,2,3,3,2,1,0},
                    "AaBbCc01233210\n"
            ),
            Arguments.of("Non-trailing left spaces of input string",
                    "   %s%d%d%d%d%d%d%d%d",
                    new Object[]{"AaBbCc",0,1,2,3,3,2,1,0},
                    "   AaBbCc01233210\n"
            ),
            Arguments.of("Non-trailing right spaces",
                    "%s%d%d%d%d%d%d%d%d   ",
                    new Object[]{"AaBbCc",0,1,2,3,3,2,1,0},
                    "AaBbCc01233210   \n"),
            Arguments.of("Keeping spaces inside input string",
                    "%s   %d%d%d%d%d%d%d%d",
                    new Object[]{"AaBbCc",0,1,2,3,3,2,1,0},
                    "AaBbCc   01233210\n"
            ),
            Arguments.of("Correct output of new-line",
                    "%n%n%nAaBbCc01233210%n%n%n",
                    null,
                    "\n\n\nAaBbCc01233210\n\n\n\n"
            ),
            Arguments.of("Correct output of new-line between other letters",
                    "AaBbCc%n%n%n01233210",
                    null,
                    "AaBbCc\n\n\n01233210\n"),
            Arguments.of("Correct output of non-letter & non-digit symbols",
                    "%s",
                    new Object[]{"()<>/.,?!#@%^&*{}[]|"},
                    "()<>/.,?!#@%^&*{}[]|\n"),
            Arguments.of("Correct output on unicode letters",
                    "%s",
                    new Object[]{"← → ↔ ↑ ↓ ↕ ↖ ↗ ↘ ↙ ⤡ ⤢"},
                    "← → ↔ ↑ ↓ ↕ ↖ ↗ ↘ ↙ ⤡ ⤢\n")
    );

    private static Stream<Arguments> provideTestData() {
        return testData;
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("ru.otus.hw.service.ioservice.PrintFormattedLineTest#provideTestData")
    void printFormattedContentTest(String nameOfTheTest, String fmtTemplate, Object[] valuePassed, String valueExpected){
        assertNotNull(this.ioService);
        assertNotNull(this.fakeStdOut);
        assertNotNull(this.fakeStdErr);
        this.ioService.printFormattedLine(fmtTemplate, valuePassed);
        this.fakeStdOut.flush();
        this.fakeStdErr.flush();
        assertEquals(Normalizer.newLine(valueExpected), this.fakeStdOut.getContent());
        assertEquals("", this.fakeStdErr.getContent());
    }
}
