DROP SCHEMA IF EXISTS tax_accounting CASCADE ;
CREATE SCHEMA tax_accounting;

CREATE TABLE tax_accounting.accounts(
    account_id BIGINT PRIMARY KEY,
    holder_name VARCHAR(100),
    balance DOUBLE PRECISION,
    CONSTRAINT account_balance_is_always_positive CHECK(balance >= 0),
    CONSTRAINT holder_non_empty CHECK (LENGTH(holder_name) > 0)
);

CREATE TABLE tax_accounting.payments (
    payment_id BIGINT PRIMARY KEY,
    account_id BIGINT,
    amount DOUBLE PRECISION,
    effective_date TIMESTAMP WITH TIME ZONE,
    CONSTRAINT payment_amount_is_always_positive CHECK(amount >= 0),
    FOREIGN KEY (account_id) REFERENCES tax_accounting.accounts(account_id)
);

CREATE TABLE tax_accounting.accruals (
    accrual_id BIGINT PRIMARY KEY,
    account_id BIGINT,
    debt_type INT,
    amount DOUBLE PRECISION,
    effective_date TIMESTAMP WITH TIME ZONE,
    CONSTRAINT accrual_amount_is_always_positive CHECK(amount >= 0),
    FOREIGN KEY (account_id) REFERENCES tax_accounting.accounts(account_id)
);

