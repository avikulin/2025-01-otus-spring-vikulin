package ru.otus.hw.repositories;

import base.ConfigurableByPropertiesTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.otus.hw.config.AppConfig;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.contracts.BookRepository;
import ru.otus.hw.repositories.data.BooksArgProvider;
import ru.otus.hw.utils.sql.SqlNormalizer;
import ru.otus.hw.utils.validators.BookValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest()
@DisplayName("Negative test for the <Books> repository")
@ContextConfiguration(classes = BookRepositoryNegativeTest.TestConfig.class)
class BookRepositoryNegativeTest extends ConfigurableByPropertiesTestBase {
    @Autowired
    BookRepository bookRepository;

    @Configuration
    @Import({JdbcBookRepository.class, JdbcAuthorRepository.class, JdbcGenreRepository.class,
            BookConverter.class, AuthorConverter.class, GenreConverter.class,
            BookValidator.class, SqlNormalizer.class})
    @EnableConfigurationProperties(AppConfig.class)
    public static class TestConfig {}

    //-----SELECT------
    @Test
    @DisplayName("Throws on trying to find by not existing ID tag")
    void throwsOnFindingByNotExistingId() {
        assertThrows(EntityNotFoundException.class, ()->bookRepository.findById(555L));
    }

    @Test
    @DisplayName("Throws on trying to find by impossible ID value")
    void throwsOnFindingByImpossibleId() {
        assertThrows(EntityValidationException.class, ()->bookRepository.findById(0L));
    }

    //-----UPDATE------
    @DisplayName("Throws on updating the book's title to null")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnUpdatingTitleWithNull(String testName, Book expected) {
        expected.setTitle(null);
        assertThrows(AppInfrastructureException.class, ()-> bookRepository.save(expected));
    }

    @DisplayName("Throws on updating the book's title to empty string")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnUpdatingTitleWithEmptyString(String testName, Book expected) {
        expected.setTitle("");
        assertThrows(AppInfrastructureException.class, ()-> bookRepository.save(expected));
    }

    @DisplayName("Throws on updating the book's year of published to negative value")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnUpdatingYearOfPublishedByNegativeValue(String testName, Book expected) {
        expected.setYearOfPublished(-1);
        assertThrows(AppInfrastructureException.class, ()-> bookRepository.save(expected));
    }

    @DisplayName("Throws on adding duplicated author for the existing book")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnAddingDuplicatedAuthor(String testName, Book expected) {
        var duplicatedAuthor = expected.getAuthors().get(0);
        var newAuthorsList = new ArrayList<>(expected.getAuthors());
        newAuthorsList.add(duplicatedAuthor);
        var newBook = new Book(expected.getId(),
                               expected.getTitle(),
                               expected.getYearOfPublished(),
                               newAuthorsList,
                               expected.getGenres()
        );
        assertThrows(AppInfrastructureException.class, ()-> bookRepository.save(newBook));
    }

    @DisplayName("Throws on adding duplicated genre for the existing book")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnAddingDuplicatedGenre(String testName, Book expected) {
        var duplicatedGenre = expected.getGenres().get(0);
        var newGenresList = new ArrayList<>(expected.getGenres());
        newGenresList.add(duplicatedGenre);
        var newBook = new Book(expected.getId(),
                expected.getTitle(),
                expected.getYearOfPublished(),
                expected.getAuthors(),
                newGenresList
        );
        assertThrows(AppInfrastructureException.class, ()-> bookRepository.save(newBook));
    }

    //-----INSERT------
    @DisplayName("Throws on inserting the book without title (null)")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnInsertingWithTitleWithNull(String testName, Book expected) {
        expected.setId(0);
        expected.setTitle(null);
        assertThrows(AppInfrastructureException.class, ()-> bookRepository.save(expected));
    }

    @DisplayName("Throws on inserting the book without title (empty string)")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnInsertingWithTitleWithEmptyString(String testName, Book expected) {
        expected.setId(0);
        expected.setTitle("");
        assertThrows(AppInfrastructureException.class, ()-> bookRepository.save(expected));
    }

    @DisplayName("Throws on inserting the book, which year of published by negative value")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnInsertingWithYearOfPublishedByNegativeValue(String testName, Book expected) {
        expected.setId(0);
        expected.setYearOfPublished(-1);
        assertThrows(AppInfrastructureException.class, ()-> bookRepository.save(expected));
    }

    @DisplayName("Throws on inserting the book with duplicated author")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnInsertingWithDuplicatedAuthor(String testName, Book expected) {
        var duplicatedAuthor = expected.getAuthors().get(0);
        var newAuthorsList = new ArrayList<>(expected.getAuthors());
        newAuthorsList.add(duplicatedAuthor);
        var newBook = new Book(0,
                expected.getTitle(),
                expected.getYearOfPublished(),
                newAuthorsList,
                expected.getGenres()
        );
        assertThrows(AppInfrastructureException.class, ()-> bookRepository.save(newBook));
    }

    @DisplayName("Throws on inserting the book with duplicated genre")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnInsertingWithDuplicatedGenre(String testName, Book expected) {
        var duplicatedGenre = expected.getGenres().get(0);
        var newGenresList = new ArrayList<>(expected.getGenres());
        newGenresList.add(duplicatedGenre);
        var newBook = new Book(0,
                expected.getTitle(),
                expected.getYearOfPublished(),
                expected.getAuthors(),
                newGenresList
        );
        assertThrows(AppInfrastructureException.class, ()-> bookRepository.save(newBook));
    }

    //-----DELETE------
    @DisplayName("Throws on deleting the book, which ID is not exists")
    @Test
    void throwsOnDeleteByImpossibleId() {
        assertThrows(EntityValidationException.class, ()-> bookRepository.deleteById(0L));
    }

    @DisplayName("Throws on deleting the book, which ID is not exists")
    @Test
    void throwsOnDeleteByIdWhichNotExists() {
        assertThrows(EntityNotFoundException.class, ()-> bookRepository.deleteById(666L));
    }
}