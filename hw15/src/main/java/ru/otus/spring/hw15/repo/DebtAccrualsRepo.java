package ru.otus.spring.hw15.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.spring.hw15.model.DebtAccrual;

@Repository
public interface DebtAccrualsRepo extends CrudRepository<DebtAccrual, Long> {}
