package ru.otus.hw.repositories.contracts;

import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository {
    List<Author> findAll();

    Optional<Author> findById(long id);
}
