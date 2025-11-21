package ru.otus.spring.hw15.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.spring.hw15.model.TaxAccount;

@Repository
public interface AccountsRepo extends CrudRepository<TaxAccount, Long> {}
