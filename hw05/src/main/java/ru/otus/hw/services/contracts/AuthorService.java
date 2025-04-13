package ru.otus.hw.services.contracts;

import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Set;

public interface AuthorService {
    List<Author> findAll();
    List<Author> findAllByIds(Set<Long> ids);
    Author findById(long id);
}
