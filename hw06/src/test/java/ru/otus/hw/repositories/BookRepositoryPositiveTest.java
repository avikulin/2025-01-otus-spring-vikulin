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
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.contracts.BookRepository;
import ru.otus.hw.repositories.data.BooksArgProvider;
import ru.otus.hw.repositories.data.TestDataProvider;
import ru.otus.hw.utils.sql.SqlNormalizer;
import ru.otus.hw.utils.validators.BookValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest()
@DisplayName("Positive tests for the <Books> repository")
@ContextConfiguration(classes = BookRepositoryPositiveTest.TestConfig.class)
class BookRepositoryPositiveTest extends ConfigurableByPropertiesTestBase {
    @Autowired
    BookRepository bookRepository;

    @Configuration
    @Import({JpaBookRepository.class, JpaAuthorRepository.class, JpaGenreRepository.class,
             BookConverter.class, AuthorConverter.class, GenreConverter.class,
             BookValidator.class, SqlNormalizer.class})
    @EnableConfigurationProperties(AppConfig.class)
    public static class TestConfig {}

    @DisplayName("Getting the certain book from DB by ID tag")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void findById(String testName, Book expected) {
        var bookId = expected.getId();
        var bookOpt = bookRepository.findById(bookId);
        assertTrue(bookOpt.isPresent());
        var book = bookOpt.get();
        assertEquals(expected, book);
    }

    @Test
    @DisplayName("Getting all the books from DB")
    void findAll() {
        var expected = TestDataProvider.getTestBooks();
        var books = bookRepository.findAll();
        assertIterableEquals(expected, books);
    }

    @DisplayName("Updating the book's title")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void updateTitle(String testName, Book expected) {
        // изменяем объект в БД
        expected.setTitle(expected.getTitle()+"-new");
        bookRepository.save(expected);

        // ищем объект в БД до ИД
        var bookId = expected.getId();
        var bookOpt = bookRepository.findById(bookId);
        assertTrue(bookOpt.isPresent());
        var book = bookOpt.get();

        // проверяем на идентичность
        assertEquals(expected, book);
    }

    @DisplayName("Updating the book's year of published")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void updateYearOfPublished(String testName, Book expected) {
        // изменяем объект в БД
        expected.setYearOfPublished(expected.getYearOfPublished()+1);
        bookRepository.save(expected);

        // ищем объект в БД до ИД
        var bookId = expected.getId();
        var bookOpt = bookRepository.findById(bookId);
        assertTrue(bookOpt.isPresent());
        var book = bookOpt.get();

        // проверяем на идентичность
        assertEquals(expected, book);
    }

    @DisplayName("Adding the new author to the book's list of authors")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void addAuthor(String testName, Book expected) {
        // изменяем объект в БД
        var newAuthor = TestDataProvider.getTestAuthorById(3);
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

    @DisplayName("Replacing the book's list of existing authors with the new author")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void replaceAuthors(String testName, Book expected) {
        // изменяем объект в БД
        var newAuthor = TestDataProvider.getTestAuthorById(3);
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

    @DisplayName("Adding the new genre to the book's list of existing authors")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void addGenre(String testName, Book expected) {
        // изменяем объект в БД
        var newGenre = TestDataProvider.getTestGenreById(3);
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

    @DisplayName("Replacing the book's list of existing authors with the new author")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void replaceGenres(String testName, Book expected) {
        // изменяем объект в БД
        var newGenre = TestDataProvider.getTestGenres().get(2);
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

    @DisplayName("Inserting the brand-new book into the DB")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void addNewBook(String testName, Book expected) {
        // создаем объект в БД
        var newBook = new Book(0,
                expected.getTitle() + "-new",
                expected.getYearOfPublished() + 10,
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


    @DisplayName("Deleting the book from DB by ID tag")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void deleteById(String testName, Book expected) {
        // удаляем конкретную книгу
        var bookID = expected.getId();
        bookRepository.deleteById(bookID);

        // проверяем, что удалилась только она
        var expectedListOfBooks = TestDataProvider.getTestBooks()
                                             .stream()
                                             .filter(book -> book.getId() != bookID)
                                             .toList();

        var booksFromDB = bookRepository.findAll();
        assertIterableEquals(expectedListOfBooks, booksFromDB);
    }
}