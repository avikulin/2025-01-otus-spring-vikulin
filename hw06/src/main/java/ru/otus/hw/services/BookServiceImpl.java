package ru.otus.hw.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.contracts.AuthorRepository;
import ru.otus.hw.repositories.contracts.BookRepository;
import ru.otus.hw.repositories.contracts.GenreRepository;
import ru.otus.hw.services.contracts.BookService;
import ru.otus.hw.utils.validators.BookValidator;

import java.util.HashSet;
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
    @Transactional(readOnly = true)
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
        return save(0L, title, yearOfPublished, authorIds, genresIds);
    }

    @Override
    @Transactional
    public Book update(long id, String title, int yearOfPublished, Set<Long> authorId, Set<Long> genresIds) {
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
        var book = new Book(id, title, yearOfPublished);
        var authors = CollectionUtils.isEmpty(authorIds) ?
                            new HashSet<Author>() :
                            authorRepository.findAllByIds(authorIds)
                                            .stream()
                                            .collect(Collectors.toUnmodifiableSet());
        var genres = CollectionUtils.isEmpty(genresIds) ?
                            new HashSet<Genre>() :
                            genreRepository.findAllByIds(genresIds)
                                           .stream()
                                           .collect(Collectors.toUnmodifiableSet());
        book.getAuthors().addAll(authors);
        book.getGenres().addAll(genres);
        this.bookValidator.validate(book); // если что не так - вылетит ошибка (вся валидация в одном месте)
        return bookRepository.save(book);
    }
}
