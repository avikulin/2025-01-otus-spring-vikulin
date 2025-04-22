package ru.otus.hw.services.helper;

import lombok.SneakyThrows;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookServiceImpl;
import utils.contracts.BookChecker;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
public class BookTestOperation {
    @Autowired
    BookServiceImpl bookService;

    @Autowired
    BookConverter bookConverter;

    @Autowired
    BookChecker bookChecker;

    public enum OpType{
        GET,
        UPDATE,
        INSERT
    }

    @SneakyThrows
    public void performOperationAndCheck(Book book, OpType opType, boolean useAsserts) {
        var bookId = book.getId();
        var bookTitle = book.getTitle();
        var yearOfPublished = book.getYearOfPublished();
        var authors = book.getAuthors().stream().map(Author::getId).collect(Collectors.toSet());
        var genres = book.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        ThrowingSupplier<Book> func;
        switch (opType){
            case GET -> func = ()->bookService.findById(bookId).orElseThrow();
            case INSERT -> func = ()->bookService.insert(bookTitle, yearOfPublished, authors, genres);
            case UPDATE -> func = ()->bookService.update(bookId, bookTitle, yearOfPublished, authors, genres);
            default -> throw new UnsupportedOperationException("Unknown operation to perform");
        }
        var actual = useAsserts ? assertDoesNotThrow(func) : func.get();
        if (useAsserts) {
            assertTrue(this.bookChecker.areEqualIgnoringComments(book, actual));
            assertDoesNotThrow(() -> this.bookConverter.bookToString(actual));
        }
    }
}
