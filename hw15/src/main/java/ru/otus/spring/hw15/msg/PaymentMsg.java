package ru.otus.spring.hw15.msg;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class PaymentMsg {
    long paymentId;
    long accountId;
    double amount;
    OffsetDateTime effectiveDate;
}
