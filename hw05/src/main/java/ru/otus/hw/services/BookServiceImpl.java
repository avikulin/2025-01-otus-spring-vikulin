package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.contracts.AuthorRepository;
import ru.otus.hw.repositories.contracts.BookRepository;
import ru.otus.hw.repositories.contracts.GenreRepository;
import ru.otus.hw.services.contracts.BookService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

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
        if (!StringUtils.hasLength(title)) {
            throw new IllegalArgumentException("Title cannot be omitted or kept empty");
        }
        if (yearOfPublished < 0) {
            throw new IllegalArgumentException("Year of published cannot negative");
        }
        if (CollectionUtils.isEmpty(authorIds)) {
            throw new IllegalArgumentException("Authors ids must not be null");
        }
        if (CollectionUtils.isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        var authors = authorRepository.findAllByIds(authorIds);
        if (CollectionUtils.isEmpty(authors) || authorIds.size() != authors.size()) {
            throw new EntityNotFoundException("One or all authors with ids %s not found".formatted(genresIds));
        }
        var genres = genreRepository.findAllByIds(genresIds);
        if (CollectionUtils.isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }
        var book = new Book(id, title, yearOfPublished, authors, genres);
        return bookRepository.save(book);
    }
}
