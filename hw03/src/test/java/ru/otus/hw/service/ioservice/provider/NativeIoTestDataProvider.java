package ru.otus.hw.service.ioservice.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class NativeIoTestDataProvider implements ArgumentsProvider {
    static String MSG_PATTERN_NEWLINE = "%n";
    static Pattern newLinePattern = Pattern.compile(MSG_PATTERN_NEWLINE);


    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of("No extra characters in output",
                                                "{0}",
                                                new Object[]{""},
                                                ""
                ),
                Arguments.of("Char-to-char equality between input & output",
                                        "{0}{1}{2}{3}{4}{5}{6}{7}{8}",
                                        new Object[]{"AaBbCc",0,1,2,3,3,2,1,0},
                                        "AaBbCc01233210"
                ),
                Arguments.of("Non-trailing left spaces of input string",
                                        "   {0}{1}{2}{3}{4}{5}{6}{7}{8}",
                                        new Object[]{"AaBbCc",0,1,2,3,3,2,1,0},
                                        "   AaBbCc01233210"
                ),
                Arguments.of("Non-trailing right spaces",
                                        "{0}{1}{2}{3}{4}{5}{6}{7}{8}   ",
                                        new Object[]{"AaBbCc",0,1,2,3,3,2,1,0},
                                        "AaBbCc01233210   "
                ),
                Arguments.of("Keeping spaces inside input string",
                                        "{0}   {1}{2}{3}{4}{5}{6}{7}{8}",
                                        new Object[]{"AaBbCc",0,1,2,3,3,2,1,0},
                                        "AaBbCc   01233210"
                ),
                Arguments.of("Correct output of new-line",
                                        "%n%n%nAaBbCc01233210%n%n%n",
                                        new Object[]{},
                                        System.lineSeparator().repeat(3) + "AaBbCc01233210" +
                                        System.lineSeparator().repeat(3)
                ),
                Arguments.of("Correct output of new-line between other letters",
                                        "AaBbCc%n%n%n01233210",
                                        new Object[]{},
                                        "AaBbCc"+System.lineSeparator().repeat(3)+"01233210"
                ),
                Arguments.of("Correct output of non-letter & non-digit symbols",
                                        "{0}",
                                        new Object[]{"()<>/.,?!#@_^&*{}[]|"},
                                        "()<>/.,?!#@_^&*{}[]|"
                ),
                Arguments.of("Correct output on unicode letters",
                                        "{0}",
                                        new Object[]{"← → ↔ ↑ ↓ ↕ ↖ ↗ ↘ ↙ ⤡ ⤢"},
                                        "← → ↔ ↑ ↓ ↕ ↖ ↗ ↘ ↙ ⤡ ⤢"
                )
        );
    }

    public static String normalizeNewLinePatter(String source) {
        return newLinePattern.matcher(source).replaceAll(System.lineSeparator());
    }
}
