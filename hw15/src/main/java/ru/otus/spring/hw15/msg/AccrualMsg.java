package ru.otus.spring.hw15.msg;

import ru.otus.spring.hw15.classifiers.DebtType;

import java.time.OffsetDateTime;

public class AccrualMsg {
    long accrualId;

    long accountId;

    DebtType debtType;

    double amount;

    OffsetDateTime effectiveDate;
}
