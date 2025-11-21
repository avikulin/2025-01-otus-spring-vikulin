package ru.otus.spring.hw15.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.spring.hw15.classifiers.DebtType;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Table(name = "ACCRUALS")
@NoArgsConstructor
public class DebtAccrual {
    @Id
    long accrualId;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    TaxAccount taxAccount;

    DebtType debtType;

    double amount;

    OffsetDateTime effectiveDate;
}
