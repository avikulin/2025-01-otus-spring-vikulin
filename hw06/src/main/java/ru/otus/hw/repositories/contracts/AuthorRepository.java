package ru.otus.hw.repositories.contracts;

import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AuthorRepository {

    List<Author> findAll();

    Optional<Author> findById(long id);

    List<Author> findAllByIds(Set<Long> ids);
}
