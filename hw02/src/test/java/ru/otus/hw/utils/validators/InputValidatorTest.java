package ru.otus.hw.utils.validators;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.exceptions.IncorrectAnswerException;
import ru.otus.hw.utils.validators.config.ValidatorsContextConfiguration;
import ru.otus.hw.utils.validators.contract.InputValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:/test-application.properties")
@ContextConfiguration(classes = ValidatorsContextConfiguration.class)
class InputValidatorTest {
    @Autowired
    InputValidator inputValidator;

    @Test
    @DisplayName("Throws on empty value")
    void checkEmptyValue() {
        assertThrows(IncorrectAnswerException.class, ()->this.inputValidator.checkIndexValues(1,10, List.of()));
    }

    @Test
    @DisplayName("Throws on doubled values")
    void checkDoubledValues1() {
        assertThrows(IncorrectAnswerException.class, ()->this.inputValidator.checkIndexValues(1,10, List.of(2,2)));
    }

    @Test
    @DisplayName("Throws on pair of doubled values")
    void checkDoubledValues2() {
        assertThrows(IncorrectAnswerException.class, ()->this.inputValidator.checkIndexValues(1,10, List.of(2,2,3,3)));
    }

    @Test
    @DisplayName("Throws on pair of doubled values with extra unique value")
    void checkDoubledValues3() {
        assertThrows(IncorrectAnswerException.class, ()->this.inputValidator.checkIndexValues(1,10, List.of(2,2,4,3,3,9)));
    }

    @Test
    @DisplayName("Throws on lower limit crossing")
    void checkBoundsCrossing1() {
        assertThrows(IncorrectAnswerException.class, ()->this.inputValidator.checkIndexValues(1,10, List.of(0,2)));
    }

    @Test
    @DisplayName("Throws on upper limit crossing")
    void checkBoundsCrossing2() {
        assertThrows(IncorrectAnswerException.class, ()->this.inputValidator.checkIndexValues(1,10, List.of(2,12)));
    }

    @Test
    @DisplayName("Throwing on both limits crossing #1")
    void checkBoundsCrossing3() {
        assertThrows(IncorrectAnswerException.class, ()->this.inputValidator.checkIndexValues(1,10, List.of(-2,12)));
    }

    @Test
    @DisplayName("Throwing on both limits crossing #2")
    void checkBoundsCrossing4() {
        assertThrows(IncorrectAnswerException.class, ()->this.inputValidator.checkIndexValues(1,10, List.of(-2,4,5,12)));
    }

    @Test
    @DisplayName("Accepts one value equals to the lower the limits")
    void checkBoundsExact1() {
        assertDoesNotThrow(()->this.inputValidator.checkIndexValues(1,10, List.of(1)));
    }

    @Test
    @DisplayName("Accepts one value equals to the upper the limits")
    void checkBoundsExact2() {
        assertDoesNotThrow(()->this.inputValidator.checkIndexValues(1,10, List.of(10)));
    }

    @Test
    @DisplayName("Accepts a pair of values, which are equal to the both the limits")
    void checkBoundsExact3() {
        assertDoesNotThrow(()->this.inputValidator.checkIndexValues(1,10, List.of(1,10)));
    }

    @Test
    @DisplayName("Accepts a unique values inside the limits")
    void checkOnePositive() {
        assertDoesNotThrow(()->this.inputValidator.checkIndexValues(1,10, List.of(4)));
    }

    @Test
    @DisplayName("Accepts multiple unique values inside the limits")
    void checkMultiplePositive() {
        assertDoesNotThrow(()->this.inputValidator.checkIndexValues(1,10, List.of(2,3,4,5)));
    }
}