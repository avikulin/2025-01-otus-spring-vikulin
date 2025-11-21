package ru.otus.spring.hw15.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Table(name = "PAYMENTS")
@NoArgsConstructor
public class Payment {
    @Id
    long paymentId;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    TaxAccount taxAccount;

    double amount;

    OffsetDateTime effectiveDate;
}
