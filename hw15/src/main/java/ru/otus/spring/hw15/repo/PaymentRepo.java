package ru.otus.spring.hw15.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.spring.hw15.model.Payment;

@Repository
public interface PaymentRepo extends CrudRepository<Payment, Long> {}
