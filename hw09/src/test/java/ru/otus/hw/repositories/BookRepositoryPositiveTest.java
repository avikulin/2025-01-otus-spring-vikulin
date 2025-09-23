package ru.otus.hw.repositories;

import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.data.BooksArgProvider;
import ru.otus.hw.data.TestDataProvider;
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
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.utils.factories.exceptions.LoggedExceptionFactoryImpl;
import ru.otus.hw.utils.validators.BookValidator;
import ru.otus.hw.utils.BookCheckerImpl;
import ru.otus.hw.utils.DeepCloneUtilsImpl;
import ru.otus.hw.utils.contracts.BookChecker;
import ru.otus.hw.utils.contracts.DeepCloneUtils;

import java.util.Comparator;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("Positive tests for the <Books> repository")
@Import({BookConverter.class, AuthorConverter.class, GenreConverter.class, CommentConverter.class,
         BookValidator.class, BookCheckerImpl.class, TestDataProvider.class, DeepCloneUtilsImpl.class,
         LoggedExceptionFactoryImpl.class})
class BookRepositoryPositiveTest extends ConfigurableByPropertiesTestBase {
    @Autowired
    BookRepository bookRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    DeepCloneUtils cloneUtils;

    @Autowired
    BookChecker bookChecker;

    @Autowired
    TestDataProvider testDataProvider;

    @DisplayName("Getting the certain book from DB by ID tag")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void findById(String testName, Book expected) {
        var bookId = expected.getId();
        var book = bookRepository.findByIdWithCheck(bookId).orElseThrow();
        assertEquals(expected, book);
        var actual = this.testEntityManager.find(Book.class, bookId);
        assertEquals(actual, book);
    }

    @Test
    @DisplayName("Getting all the books from DB")
    void findAll() {
        var expected = testDataProvider.getTestBooks();
        var books = bookRepository.findAllBatched();
        assertIterableEquals(expected, books);
        var actual = this.testEntityManager
                         .getEntityManager()
                            .createQuery("SELECT b FROM Book b", Book.class)
                                .getResultList();
        var booksIndex = books.stream().collect(Collectors.toMap(Book::getId, b->b));
        var controlSum = actual.stream().filter(a->a.equals(booksIndex.get(a.getId()))).count();
        assertEquals(actual.size(), controlSum);
    }

    @DisplayName("Updating the book's title")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void updateTitle(String testName, Book expected) {
        var bookId = expected.getId();
        var book = this.testEntityManager.find(Book.class, bookId);
        book.setTitle(expected.getTitle()+"-new");
        bookRepository.save(book);
        testEntityManager.flush(); // поможем ему определиться
        var actual = this.testEntityManager.find(Book.class, bookId);
        assertEquals(actual, book);
    }

    @DisplayName("Updating the book's year of published")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void updateYearOfPublished(String testName, Book expected) {
        var bookId = expected.getId();
        var book = this.testEntityManager.find(Book.class, bookId);
        book.setYearOfPublished(expected.getYearOfPublished()+1);
        bookRepository.save(book);
        testEntityManager.flush(); // поможем ему определиться
        var actual = this.testEntityManager.find(Book.class, bookId);
        assertEquals(actual, book);
    }

    @DisplayName("Adding the new author to the book's list of authors")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void addAuthor(String testName, Book expected) {
        var bookId = expected.getId();
        var book = this.testEntityManager.find(Book.class, bookId);
        book.getAuthors().add(testDataProvider.getTestAuthorById(3));
        bookRepository.save(book);
        testEntityManager.flush(); // поможем ему определиться
        var actual = this.testEntityManager.find(Book.class, bookId);
        assertEquals(actual, book);
    }

    @DisplayName("Replacing the book's list of existing authors with the new author")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void replaceAuthors(String testName, Book expected) {
        // достаем книжку из БД
        var bookId = expected.getId();
        var book = this.testEntityManager.find(Book.class, bookId);

        // заменяем существующих авторов на нового и сохраняем.
        var newAuthor = testEntityManager.getEntityManager().getReference(Author.class, 3);
        book.getAuthors().clear();
        book.getAuthors().add(newAuthor);
        this.bookRepository.save(book);
        this.testEntityManager.flush(); // поможем сбросить контекст

        // проверяем, что данные корректно перенесены из RAM в БД
        var actual = this.testEntityManager.find(Book.class, bookId);
        assertEquals(actual, book);

        // проверяем, что потеряшки зачищены
        var actualAuthorsKeys = this.bookChecker.getAuthorsKeys(bookId);
        var expectedAuthorsKeys = book.getAuthors()
                                      .stream()
                                      .map(Author::getId)
                                      .sorted()
                                      .map(String::valueOf)
                                      .collect(Collectors.joining(","));
        assertEquals(expectedAuthorsKeys, actualAuthorsKeys);
    }

    @DisplayName("Adding the new genre to the book's list of existing genres")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void addGenre(String testName, Book expected) {
        var bookId = expected.getId();
        var book = this.testEntityManager.find(Book.class, bookId);
        book.getGenres().add(testDataProvider.getTestGenreById(3));
        bookRepository.save(book);
        testEntityManager.flush(); // поможем ему определиться
        var actual = this.testEntityManager.find(Book.class, bookId);
        assertEquals(actual, book);
    }

    @DisplayName("Replacing the book's list of existing genres with the new genre")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void replaceGenres(String testName, Book expected) {
        // достаем книжку из БД
        var bookId = expected.getId();
        var book = this.testEntityManager.find(Book.class, bookId);

        // заменяем существующие жанры на новой и сохраняем.
        book.getGenres().add(testDataProvider.getTestGenres().get(2));
        bookRepository.save(book);
        this.testEntityManager.flush(); // поможем сбросить контекст

        // проверяем, что данные корректно перенесены из RAM в БД
        var actual = this.testEntityManager.find(Book.class, bookId);
        assertEquals(actual, book);

        // проверяем, что потеряшки зачищены
        var actualGenresKeys = this.bookChecker.getGenresKeys(bookId);
        var expectedGenresKeys = book.getGenres()
                .stream()
                .map(Genre::getId)
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        assertEquals(expectedGenresKeys, actualGenresKeys);
    }

    @DisplayName("Inserting the brand-new book into the DB")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void addNewBook(String testName, Book expected) {
        // создаем объект в БД
        var newBook = cloneUtils.cloneBookAsNew(expected);
        var tem = testEntityManager.getEntityManager();
        var managedAuthors = newBook.getAuthors()
                                    .stream()
                                    .map(a->tem.getReference(Author.class, a.getId()))
                                    .collect(Collectors.toSet());
        newBook.setAuthors(managedAuthors);
        var managedGenres = newBook.getGenres()
                                    .stream()
                                    .map(g->tem.getReference(Genre.class, g.getId()))
                                    .collect(Collectors.toSet());
        newBook.setGenres(managedGenres);

        newBook.setYearOfPublished(expected.getYearOfPublished() + 10);
        bookRepository.save(newBook);
        var bookId = expected.getId();
        var actual = this.testEntityManager.find(Book.class, bookId);
        assertTrue(this.bookChecker.areEqual(actual, expected));
    }


    @DisplayName("Deleting the book from DB by ID tag")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(BooksArgProvider.class)
    void deleteById(String testName, Book expected) {
        // удаляем конкретную книгу
        var bookId = expected.getId();
        bookRepository.deleteById(bookId);

        // проверяем, что удалилась только она
        var expectedListOfBooks = testDataProvider.getTestBooks()
                                             .stream()
                                             .filter(book -> book.getId() != bookId)
                                             .sorted(Comparator.comparingLong(Book::getId))
                                             .toList();
        var actualListOfBooks = this.testEntityManager
                .getEntityManager()
                .createQuery("SELECT b FROM Book b ORDER BY b.id", Book.class)
                .getResultList()
                    .stream()
                    .sorted(Comparator.comparingLong(Book::getId))
                    .toList();
        var expectedBooksIndex = expectedListOfBooks.stream().collect(Collectors.toMap(Book::getId, b->b));
        var controlSample = actualListOfBooks.stream()
                                  .filter(a->a.equals(expectedBooksIndex.get(a.getId())))
                                  .sorted(Comparator.comparingLong(Book::getId))
                                  .toList();
        assertIterableEquals(expectedListOfBooks, controlSample);

        // проверяем, что комменты подчистила политика CascadeType.REMOVE
        assertEquals(0, this.bookChecker.getCommentsCount(bookId));
    }
}