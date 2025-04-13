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
import ru.otus.hw.exceptions.SqlCommandFailure;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.contracts.BookRepository;
import ru.otus.hw.repositories.data.BooksArgSource;
import ru.otus.hw.repositories.data.TestData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest()
@DisplayName("Testing the <Books> repository")
@ContextConfiguration(classes = BookRepositoryNegativeTest.TestConfig.class)
class BookRepositoryNegativeTest extends ConfigurableByPropertiesTestBase {
    @Autowired
    BookRepository bookRepository;

    @Configuration
    @Import({JdbcBookRepository.class, JdbcAuthorRepository.class, JdbcGenreRepository.class,
             BookConverter.class, AuthorConverter.class, GenreConverter.class})
    @EnableConfigurationProperties(AppConfig.class)
    public static class TestConfig {}

    @Test
    @DisplayName("Throws on trying to find by not existing ID tag")
    void throwsOnFindingByNotExistingId() {
        assertThrows(EntityNotFoundException.class, ()->bookRepository.findById(555L));
    }

    @DisplayName("Throws on book's title is null")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgSource.class)
    void updateTitle(String testName, Book expected) {
        // изменяем объект в БД
        expected.setTitle(null);
        assertThrows(AppInfrastructureException.class, ()->bookRepository.save(expected));
    }

    @DisplayName("Updating year of published")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgSource.class)
    void updateYearOfPublished(String testName, Book expected) {
        // изменяем объект в БД
        expected.setYearOfPublished(expected.getYearOfPublished()+101);
        bookRepository.save(expected);

        // ищем объект в БД до ИД
        var bookId = expected.getId();
        var bookOpt = bookRepository.findById(bookId);
        assertTrue(bookOpt.isPresent());
        var book = bookOpt.get();

        // проверяем на идентичность
        assertEquals(expected, book);
    }

    @DisplayName("Adding new author to the list of existing authors")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgSource.class)
    void addAuthor(String testName, Book expected) {
        // изменяем объект в БД
        var newAuthor = TestData.getTestAuthors().get(2);
        var newAuthorsList = new ArrayList<>(expected.getAuthors());
        newAuthorsList.add(newAuthor);
        var newBook = new Book(expected.getId(),
                               expected.getTitle(),
                               expected.getYearOfPublished(),
                               newAuthorsList,
                               expected.getGenres()
        );
        bookRepository.save(newBook);

        // ищем объект в БД до ИД
        var bookId = newBook.getId();
        var bookOpt = bookRepository.findById(bookId);
        assertTrue(bookOpt.isPresent());
        var newBookFromDB = bookOpt.get();

        // проверяем на идентичность
        assertEquals(newBook, newBookFromDB);
    }

    @DisplayName("Replacing the list of existing authors with new author")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgSource.class)
    void replaceAuthors(String testName, Book expected) {
        // изменяем объект в БД
        var newAuthor = TestData.getTestAuthors().get(2);
        var newBook = new Book(expected.getId(),
                expected.getTitle(),
                expected.getYearOfPublished(),
                List.of(newAuthor),
                expected.getGenres()
        );
        bookRepository.save(newBook);

        // ищем объект в БД до ИД
        var bookId = newBook.getId();
        var bookOpt = bookRepository.findById(bookId);
        assertTrue(bookOpt.isPresent());
        var newBookFromDB = bookOpt.get();

        // проверяем на идентичность
        assertEquals(newBook, newBookFromDB);
    }

    @DisplayName("Adding new genre to the list of existing authors")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgSource.class)
    void addGenre(String testName, Book expected) {
        // изменяем объект в БД
        var newGenre = TestData.getTestGenres().get(2);
        var newGenresList = new ArrayList<>(expected.getGenres());
        newGenresList.add(newGenre);
        var newBook = new Book(expected.getId(),
                expected.getTitle(),
                expected.getYearOfPublished(),
                expected.getAuthors(),
                newGenresList
        );
        bookRepository.save(newBook);

        // ищем объект в БД до ИД
        var bookId = newBook.getId();
        var bookOpt = bookRepository.findById(bookId);
        assertTrue(bookOpt.isPresent());
        var newBookFromDB = bookOpt.get();

        // проверяем на идентичность
        assertEquals(newBook, newBookFromDB);
    }

    @DisplayName("Replacing the list of existing authors with new author")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgSource.class)
    void replaceGenres(String testName, Book expected) {
        // изменяем объект в БД
        var newGenre = TestData.getTestGenres().get(2);
        var newBook = new Book(expected.getId(),
                expected.getTitle(),
                expected.getYearOfPublished(),
                expected.getAuthors(),
                List.of(newGenre)
        );
        bookRepository.save(newBook);

        // ищем объект в БД до ИД
        var bookId = newBook.getId();
        var bookOpt = bookRepository.findById(bookId);
        assertTrue(bookOpt.isPresent());
        var newBookFromDB = bookOpt.get();

        // проверяем на идентичность
        assertEquals(newBook, newBookFromDB);
    }

    @DisplayName("Inserting brand-new book into the DB")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgSource.class)
    void addNewBook(String testName, Book expected) {
        // создаем объект в БД
        var newBook = new Book(0,
                expected.getTitle() + "-new",
                expected.getYearOfPublished() + 101,
                expected.getAuthors(),
                expected.getGenres()
        );
        var bookFromDB = bookRepository.save(newBook);

        // проверяем, что вернувшийся из БД объект соответствует исходным параметрам
        var newId = bookFromDB.getId();
        newBook.setId(newId);
        assertEquals(newBook, bookFromDB);

        // ищем старый объект в БД до ИД и проверяем, что он не изменился
        var bookOpt = bookRepository.findById(expected.getId());
        assertTrue(bookOpt.isPresent());
        var notUpdatedBook = bookOpt.get();
        assertEquals(expected, notUpdatedBook);
    }


    @DisplayName("Deleting book from DB by ID tag")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgSource.class)
    void deleteById(String testName, Book expected) {
        // удаляем конкретную книгу
        var bookID = expected.getId();
        bookRepository.deleteById(bookID);

        // проверяем, что удалилась только она
        var expectedListOfBooks = TestData.getTestBooks()
                                             .stream()
                                             .filter(book -> book.getId() != bookID)
                                             .toList();

        var booksFromDB = bookRepository.findAll();
        assertIterableEquals(expectedListOfBooks, booksFromDB);
    }
}