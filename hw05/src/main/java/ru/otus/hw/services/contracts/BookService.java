package ru.otus.hw.services.contracts;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<Book> findById(long id);

    List<Book> findAll();

    Book insert(String title, int yearOfPublished, Set<Long> authorIds, Set<Long> genresIds);

    Book update(long id, String title, int yearOfPublished, Set<Long> authorIds, Set<Long> genresIds);

    void deleteById(long id);
}
