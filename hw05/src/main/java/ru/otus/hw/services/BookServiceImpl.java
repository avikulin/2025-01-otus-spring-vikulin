package ru.otus.hw.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.contracts.AuthorRepository;
import ru.otus.hw.repositories.contracts.BookRepository;
import ru.otus.hw.repositories.contracts.GenreRepository;
import ru.otus.hw.services.contracts.BookService;
import ru.otus.hw.utils.validators.BookValidator;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        bookRepository.deleteById(id);
    }

    private Book save(long id, String title, int yearOfPublished, Set<Long> authorIds, Set<Long> genresIds) {
        var authors = authorRepository.findAllByIds(authorIds);
        if (CollectionUtils.isEmpty(authors) || authorIds.size() != authors.size()) {
            throw new EntityNotFoundException("One or all authors with ids %s not found".formatted(genresIds));
        }
        var genres = genreRepository.findAllByIds(genresIds);
        if (CollectionUtils.isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }
        var book = new Book(id, title, yearOfPublished, authors, genres);
        this.bookValidator.validate(book); // если что не так - вывалится ValidationException
        return bookRepository.save(book);
    }
}
