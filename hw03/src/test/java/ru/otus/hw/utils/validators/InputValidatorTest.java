package ru.otus.hw.utils.validators;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.utils.validators.base.DefaultInputValidatorImpl;
import ru.otus.hw.utils.validators.config.ValidatorsContextConfiguration;
import ru.otus.hw.utils.validators.base.contracts.InputValidator;
import ru.otus.hw.utils.validators.providers.InputValidatorNegativeArgsProvider;
import ru.otus.hw.utils.validators.providers.InputValidatorPositiveArgsProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Check native behaviour of input validation")
@SpringBootTest(classes = ValidatorsContextConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import({DefaultInputValidatorImpl.class})
@ActiveProfiles({"test","native"})
@TestPropertySource(locations = "classpath:/test-application.yml")
class InputValidatorTest {
    @Autowired
    InputValidator inputValidator;

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(InputValidatorNegativeArgsProvider.class)
    void checkNegativeScenario(String testName, int min, int max, List<Integer> options) throws IncorrectAnswerException {
        assertThrows(IncorrectAnswerException.class, ()->this.inputValidator.checkIndexValues(min,max, options));
    }


    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(InputValidatorPositiveArgsProvider.class)
    void checkBoundsExact1(String testName, int min, int max, List<Integer> options) {
        assertDoesNotThrow(()->this.inputValidator.checkIndexValues(min,max, options));
    }
}