package ru.otus.hw.utils.validators.localized.input;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.service.ioservice.config.LocalizedIoStubsConfig;
import ru.otus.hw.utils.validators.base.DefaultInputValidatorImpl;
import ru.otus.hw.utils.validators.localized.contracts.LocalizedInputValidator;
import ru.otus.hw.utils.validators.providers.InputValidatorNegativeArgsProvider;
import ru.otus.hw.utils.validators.providers.InputValidatorPositiveArgsProvider;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Check the localized (en-US) behaviour of input validation")
@SpringBootTest(classes = LocalizedIoStubsConfig.class)
@Import(DefaultInputValidatorImpl.class)
@ActiveProfiles("localized")
@TestPropertySource(properties = "test.locale=en-US")
class LocalizedEnUsInputValidatorTest extends ConfigurableByPropertiesTestBase {
    Map<String, String> errMsg = Map.of(
            "input-validator.error.exceeds-valid-range","Variant exceeds the valid range",
            "input-validator.error.doubled-variant","Doubled variants are prohibited",
            "input-validator.error.empty-options-set","Empty options set is incorrect by default"
    );

    @Autowired
    LocalizedInputValidator inputValidator;

    @DisplayName("Negative scenarios")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(InputValidatorNegativeArgsProvider.class)
    void checkNegativeScenario(String testName, int min, int max, List<Integer> options, String errCode) {
        var ex = assertThrows(IncorrectAnswerException.class, ()->this.inputValidator.checkIndexValues(min,max, options));
        var exMsg = errMsg.get(errCode);
        assertTrue(ex.getMessage().startsWith(exMsg));
    }


    @DisplayName("Positive scenarios")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(InputValidatorPositiveArgsProvider.class)
    void checkBoundsExact1(String testName, int min, int max, List<Integer> options) {
        assertDoesNotThrow(()->this.inputValidator.checkIndexValues(min,max, options));
    }
}