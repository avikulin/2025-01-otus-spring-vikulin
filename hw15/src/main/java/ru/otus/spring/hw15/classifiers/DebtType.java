package ru.otus.spring.hw15.classifiers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum DebtType {
    PERSONAL_INCOME_TAX(DebtPriority.FIRST, "НДФЛ"),
    VALUE_ADDED_TAX(DebtPriority.SECOND, "НДС"),
    PROPERTY_TAX(DebtPriority.SECOND, "Налог на прибыль предприятий"),
    PENALTIES_AND_FINES(DebtPriority.THIRD, "Пени и штрафы");

    DebtPriority priority;
    String description;

    DebtType(DebtPriority priority, String description) {
        this.priority = priority;
        this.description = description;
    }
}
