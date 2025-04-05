package ru.otus.hw.utils.validators.providers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InputValidatorPositiveArgsProvider implements ArgumentsProvider {


    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of("Accepts one value equals to the lower the limits",
                                        1,10,
                                        List.of(1)
                ),
                Arguments.of("Accepts one value equals to the upper the limits",
                                        1,10,
                                        List.of(10)
                ),
                Arguments.of("Accepts a pair of values, which are equal to the both the limits",
                                        1,10,
                                        List.of(4)
                ),
                Arguments.of("Accepts a unique values inside the limits",
                                        1,10,
                                        List.of(1,10)
                ),
                Arguments.of("Accepts multiple unique values inside the limits",
                                        1,10,
                                        List.of(2,3,4,5)
                )
        );
    }
}
