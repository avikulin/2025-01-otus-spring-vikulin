package ru.otus.hw.services;

import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.data.BooksArgProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.contracts.BookService;
import ru.otus.hw.services.helper.BookTestOperation;
import ru.otus.hw.utils.factories.exceptions.LoggedExceptionFactoryImpl;
import ru.otus.hw.utils.validators.BookValidator;
import ru.otus.hw.utils.BookCheckerImpl;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@DisplayName("Negative tests pack for <Book> service")

//отключаем транзакции, как сказано в ДЗ, но я правда не понимаю - зачем?
@Transactional(propagation = Propagation.NOT_SUPPORTED)

@Import({BookServiceImpl.class, BookValidator.class, BookTestOperation.class, BookCheckerImpl.class,
         BookConverter.class, AuthorConverter.class, GenreConverter.class, CommentConverter.class,
          LoggedExceptionFactoryImpl.class})
class BookServiceNegativeTest extends ConfigurableByPropertiesTestBase {

    @Autowired
    BookService bookService;

    @Autowired
    BookTestOperation bookTestOperation;

    @Test
    @DisplayName("Throws on trying to find by not existing ID tag")
    void throwsOnFindingByNotExistingId() {
        assertThrows(EntityNotFoundException.class, ()->bookService.findById(555L).orElseThrow());
    }

    @Test
    @DisplayName("Throws on trying to find by impossible ID value")
    void throwsOnFindingByImpossibleId() {
        assertThrows(InvalidDataAccessApiUsageException.class, ()->bookService.findById(0L));
    }


    @DisplayName("Throws on updating the book's title to blank string")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnUpdatingTitleWithBlankString(String testName, Book expected) {
        expected.setTitle("        ");
        assertThrows(
                EntityValidationException.class,
                ()-> this.bookTestOperation.performOperationAndCheck(expected, BookTestOperation.OpType.UPDATE, false)
        );
    }

    @DisplayName("Throws on updating the book's year of published to value in future")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnUpdatingYearOfPublishedByFutureYear(String testName, Book expected) {
        var future = LocalDateTime.now().getYear() + 1;
        expected.setYearOfPublished(future);
        assertThrows(
                EntityValidationException.class,
                ()-> this.bookTestOperation.performOperationAndCheck(expected, BookTestOperation.OpType.UPDATE, false)
        );
    }

    @DisplayName("Throws on clearing the authors list of the existing book")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnClearingAuthors(String testName, Book expected) {
        expected.getAuthors().clear();
        assertThrows(
                EntityValidationException.class,
                ()-> this.bookTestOperation.performOperationAndCheck(expected, BookTestOperation.OpType.UPDATE, false)
        );
    }

    @DisplayName("Throws on clearing the genres list of the existing book")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnClearingGenres(String testName, Book expected) {
        expected.getGenres().clear();
        assertThrows(
                EntityValidationException.class,
                ()-> this.bookTestOperation.performOperationAndCheck(expected, BookTestOperation.OpType.UPDATE, false)
        );
    }

    @DisplayName("Throws on inserting the book without title (blank string)")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnInsertingWithTitleWithBlankString(String testName, Book expected) {
        expected.setTitle("        ");
        assertThrows(
                EntityValidationException.class,
                ()-> this.bookTestOperation.performOperationAndCheck(expected, BookTestOperation.OpType.INSERT, false)
        );
    }

    @DisplayName("Throws on inserting the book, which year of published by value in future")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnInsertingWithYearOfPublishedByFutureYear(String testName, Book expected) {
        var future = LocalDateTime.now().getYear() + 1;
        expected.setYearOfPublished(future);
        assertThrows(
                EntityValidationException.class,
                ()-> this.bookTestOperation.performOperationAndCheck(expected, BookTestOperation.OpType.INSERT, false)
        );
    }


    @DisplayName("Throws on inserting the book without authors (empty list)")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnInsertingWithEmptyAuthors(String testName, Book expected) {
        expected.getAuthors().clear();
        assertThrows(
                EntityValidationException.class,
                ()-> this.bookTestOperation.performOperationAndCheck(expected, BookTestOperation.OpType.INSERT, false)
        );
    }

    @DisplayName("Throws on inserting the book without genre (empty list)")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnInsertingWithEmptyGenres(String testName, Book expected) {
        expected.getGenres().clear();
        assertThrows(
                EntityValidationException.class,
                ()-> this.bookTestOperation.performOperationAndCheck(expected, BookTestOperation.OpType.INSERT, false)
        );
    }

    @Test
    @DisplayName("Throws on trying to delete by not existing ID tag")
    void throwsOnDeletingByNotExistingId() {
        assertThrows(EntityNotFoundException.class, ()->bookService.deleteById(555L));
    }

    @Test
    @DisplayName("Throws on trying to delete by impossible ID value")
    void throwsOnDeletingByImpossibleId() {
        assertThrows(EntityValidationException.class, ()->bookService.deleteById(0L));
    }
}