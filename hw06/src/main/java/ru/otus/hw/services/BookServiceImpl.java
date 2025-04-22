package ru.otus.hw.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.contracts.AuthorRepository;
import ru.otus.hw.repositories.contracts.BookRepository;
import ru.otus.hw.repositories.contracts.GenreRepository;
import ru.otus.hw.services.contracts.BookService;
import ru.otus.hw.utils.validators.BookValidator;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookServiceImpl implements BookService {

    AuthorRepository authorRepository;

    GenreRepository genreRepository;

    BookRepository bookRepository;

    BookValidator bookValidator;

    @Override
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional
    public Book insert(String title, int yearOfPublished, Set<Long> authorIds, Set<Long> genresIds) {
        return save(0, title, yearOfPublished, authorIds, genresIds);
    }

    @Override
    @Transactional
    public Book update(long id, String title,  int yearOfPublished, Set<Long> authorId, Set<Long> genresIds) {
        return save(id, title, yearOfPublished,  authorId, genresIds);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        if (id < 1) {
            log.error("Trying to process impossible value as ID for delete operation");
            throw new EntityValidationException("The ID must be 1 or greater");
        }
        bookRepository.deleteById(id);
    }

    private Book save(long id, String title, int yearOfPublished, Set<Long> authorIds, Set<Long> genresIds) {
        Validate.isTrue(id>=0, "ID must not be negative");
        Validate.notBlank(title, "Title cannot be and empty/blank string");
        Validate.notEmpty(authorIds,"Author IDs cannot be an empty collection");
        Validate.notEmpty(genresIds,"Genre IDs cannot be an empty collection");
        var authors = authorRepository.findAllByIds(authorIds).stream().collect(Collectors.toUnmodifiableSet());
        var genres = genreRepository.findAllByIds(genresIds).stream().collect(Collectors.toUnmodifiableSet());
        var book = new Book(id, title, yearOfPublished, authors, genres, Set.of());
        this.bookValidator.validate(book); // если что не так - вылетит ошибка
        return bookRepository.save(book);
    }
}
