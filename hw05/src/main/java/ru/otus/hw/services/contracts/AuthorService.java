package ru.otus.hw.services.contracts;

import ru.otus.hw.models.Author;

import java.util.List;

public interface AuthorService {
    List<Author> findAll();
    Author findById(long id);
}
