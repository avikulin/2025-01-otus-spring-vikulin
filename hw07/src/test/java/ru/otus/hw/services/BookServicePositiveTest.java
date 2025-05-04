package ru.otus.hw.services;

import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.data.BooksArgProvider;
import ru.otus.hw.data.TestDataProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.helper.BookTestOperation;
import ru.otus.hw.services.helper.BookTestOperation.OpType;
import ru.otus.hw.utils.factories.exceptions.LoggedExceptionFactoryImpl;
import ru.otus.hw.utils.validators.BookValidator;
import ru.otus.hw.utils.BookCheckerImpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DataJpaTest
@DisplayName("Positive tests pack for <Book> service")

//отключаем транзакции, как сказано в ДЗ, но я правда не понимаю - зачем?
@Transactional(propagation = Propagation.NOT_SUPPORTED)

@Import({BookServiceImpl.class, BookValidator.class, BookTestOperation.class, BookCheckerImpl.class,
        BookConverter.class, AuthorConverter.class, GenreConverter.class, CommentConverter.class,
        LoggedExceptionFactoryImpl.class, TestDataProvider.class})
class BookServicePositiveTest extends ConfigurableByPropertiesTestBase {

    @Autowired
    BookServiceImpl bookService;

    @Autowired
    BookConverter bookConverter;

    @Autowired
    BookTestOperation bookTestOperation;

    @Autowired
    TestDataProvider testDataProvider;

    @DisplayName("Getting the certain book from DB by ID tag")
    @ParameterizedTest(name = "{0}")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @ArgumentsSource(BooksArgProvider.class)
    void findById(String testName, Book expected) {
        bookTestOperation.performOperationAndCheck(expected, OpType.GET, true);
    }

    @Test
    @DisplayName("Getting all the books from DB")
    void findAll() {
        var expected = testDataProvider.getTestBooks();
        var books = assertDoesNotThrow(()->this.bookService.findAll());
        assertIterableEquals(expected, books);
        assertDoesNotThrow(()->books.stream().map(bookConverter::bookToString));
    }

    @DisplayName("Updating the book's title")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void updateTitle(String testName, Book expected) {
        expected.setTitle(expected.getTitle()+"-new");
        this.bookTestOperation.performOperationAndCheck(expected, OpType.UPDATE, true);
    }

    @DisplayName("Updating the book's year of published")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void updateYearOfPublished(String testName, Book expected) {
        expected.setYearOfPublished(expected.getYearOfPublished()+1);
        this.bookTestOperation.performOperationAndCheck(expected, OpType.UPDATE, true);
    }

    @DisplayName("Adding the new author to the book's list of authors")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void addAuthor(String testName, Book expected) {
        expected.getAuthors().add(testDataProvider.getTestAuthorById(3));
        this.bookTestOperation.performOperationAndCheck(expected, OpType.UPDATE, true);
    }

    @DisplayName("Replacing the book's list of existing authors with the new author")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void replaceAuthors(String testName, Book expected) {
        expected.getAuthors().clear();
        expected.getAuthors().add(testDataProvider.getTestAuthorById(3));
        this.bookTestOperation.performOperationAndCheck(expected, OpType.UPDATE, true);
    }

    @DisplayName("Adding the new genre to the book's list of existing genres")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void addGenre(String testName, Book expected) {
        expected.getGenres().add(testDataProvider.getTestGenreById(3));
        this.bookTestOperation.performOperationAndCheck(expected, OpType.UPDATE, true);
    }

    @DisplayName("Replacing the book's list of existing genres with the new genre")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void replaceGenres(String testName, Book expected) {
        expected.getGenres().add(testDataProvider.getTestGenres().get(2));
        this.bookTestOperation.performOperationAndCheck(expected, OpType.UPDATE, true);
    }

    @DisplayName("Inserting the brand-new book into the DB")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void addNewBook(String testName, Book expected) {
        this.bookTestOperation.performOperationAndCheck(expected, OpType.INSERT, true);
    }

    @DisplayName("Deleting the book from DB by ID tag")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void deleteById(String testName, Book expected) {
        assertDoesNotThrow(()->this.bookService.deleteById(expected.getId()));
    }
}