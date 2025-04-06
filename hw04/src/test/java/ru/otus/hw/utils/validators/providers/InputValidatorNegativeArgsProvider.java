package ru.otus.hw.utils.validators.providers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InputValidatorNegativeArgsProvider implements ArgumentsProvider {

    String ERR_CODE_EXCEEDS_VALID_RANGE = "input-validator.error.exceeds-valid-range";
    String ERR_CODE_DOUBLED_VARIANT = "input-validator.error.doubled-variant";
    String ERR_CODE_EMPTY_OPTIONS_SET = "input-validator.error.empty-options-set";

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of("Throws on empty value",
                                        1, 10,
                                        List.of(),
                                        ERR_CODE_EMPTY_OPTIONS_SET
                ),
                Arguments.of("Throws on doubled values",
                                        1,10,
                                        List.of(2,2),
                                        ERR_CODE_DOUBLED_VARIANT
                ),
                Arguments.of("Throws on pair of doubled values",
                                        1,10,
                                        List.of(2,2,3,3),
                                        ERR_CODE_DOUBLED_VARIANT
                ),
                Arguments.of("Throws on pair of doubled values with extra unique values",
                                        1,10,
                                        List.of(2,2,4,3,3,9),
                                        ERR_CODE_DOUBLED_VARIANT
                ),
                Arguments.of("Throws on lower limit crossing",
                                        1,10,
                                        List.of(0,2),
                                        ERR_CODE_EXCEEDS_VALID_RANGE
                ),
                Arguments.of("Throws on upper limit crossing",
                                        1,10,
                                        List.of(2,12),
                                        ERR_CODE_EXCEEDS_VALID_RANGE
                ),
                Arguments.of("Throwing on both limits crossing #1",
                                        1,10,
                                        List.of(-2,12),
                                        ERR_CODE_EXCEEDS_VALID_RANGE
                ),
                Arguments.of("Throwing on both limits crossing #2",
                                        1,10,
                                        List.of(-2,4,5,12),
                                        ERR_CODE_EXCEEDS_VALID_RANGE
                )
        );
    }
}
