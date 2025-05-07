package ru.otus.hw.repositories.contracts;

import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

public interface GenreRepository {

    List<Genre> findAll();

    Genre findById(long id);

    List<Genre> findAllByIds(Set<Long> ids);
}
