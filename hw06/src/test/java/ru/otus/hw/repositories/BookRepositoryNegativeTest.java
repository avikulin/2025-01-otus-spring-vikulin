package ru.otus.hw.repositories;

import base.ConfigurableByPropertiesTestBase;
import data.BooksArgProvider;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.contracts.BookRepository;
import ru.otus.hw.utils.factories.exceptions.LoggedExceptionFactoryImpl;
import ru.otus.hw.utils.validators.BookValidator;
import utils.DeepCloneUtilsImpl;
import utils.contracts.DeepCloneUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;

/*
    Поскольку контейнер связей был заменен на Set<T>,
    то следующие тесты (реализованные в предыдущем ДЗ) потеряли свою актуальность:
    1) throwsOnAddingDuplicatedGenre         (Throws on adding duplicated genre for the existing book)
    2) throwsOnAddingDuplicatedAuthor        (Throws on adding duplicated author for the existing book)
    3) throwsOnInsertingWithDuplicatedAuthor (Throws on inserting the book with duplicated author)
    4) throwsOnInsertingWithDuplicatedGenre  (Throws on inserting the book with duplicated genre)
 */

@DataJpaTest
@DisplayName("Negative test for the <Books> repository")
@Import({JpaBookRepository.class, JpaAuthorRepository.class, JpaGenreRepository.class,
        BookConverter.class, AuthorConverter.class, GenreConverter.class, CommentConverter.class,
        BookValidator.class, DeepCloneUtilsImpl.class, LoggedExceptionFactoryImpl.class})
class BookRepositoryNegativeTest extends ConfigurableByPropertiesTestBase {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    DeepCloneUtils cloneUtils;

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
        assertThrows(NullPointerException.class, ()-> expected.setTitle(null));
    }

    @DisplayName("Throws on updating the book's title to empty string")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnUpdatingTitleWithEmptyString(String testName, Book expected) {
        expected.setTitle("");
        assertThrows(ConstraintViolationException.class,
            ()-> {
                bookRepository.save(expected);
                this.testEntityManager.flush();
            }
        );
    }

    @DisplayName("Throws on updating the book's year of published to negative value")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnUpdatingYearOfPublishedByNegativeValue(String testName, Book expected) {
        var book = this.testEntityManager.find(Book.class, expected.getId());
        book.setYearOfPublished(-1);
        assertThrows(AppInfrastructureException.class,
            ()-> {
                bookRepository.save(book);
                this.testEntityManager.flush();
            }
        );
    }

    //-----INSERT------
    @DisplayName("Throws on inserting the book without title (null)")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnInsertingWithTitleWithNull(String testName, Book expected) {
        var newBook = this.cloneUtils.cloneBookAsNew(expected);
        assertThrows(NullPointerException.class, ()-> newBook.setTitle(null));
    }

    @DisplayName("Throws on inserting the book without title (empty string)")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnInsertingWithTitleWithEmptyString(String testName, Book expected) {
        var newBook = this.cloneUtils.cloneBookAsNew(expected);
        newBook.setTitle("");
        assertThrows(AppInfrastructureException.class, ()-> bookRepository.save(newBook));
    }

    @DisplayName("Throws on inserting the book, which year of published by negative value")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void throwsOnInsertingWithYearOfPublishedByNegativeValue(String testName, Book expected) {
        var newBook = this.cloneUtils.cloneBookAsNew(expected);
        newBook.setYearOfPublished(-1);
        assertThrows(AppInfrastructureException.class, ()-> bookRepository.save(newBook));
    }


    //-----DELETE------
    @DisplayName("Throws on deleting the book, with impossible ID value")
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