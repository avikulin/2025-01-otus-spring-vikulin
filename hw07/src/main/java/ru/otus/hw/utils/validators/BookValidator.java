package ru.otus.hw.utils.validators;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.contracts.CatalogEntity;
import ru.otus.hw.utils.factories.exceptions.contracts.LoggedExceptionFactory;
import ru.otus.hw.utils.validators.contracts.EntityValidator;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookValidator implements EntityValidator {

    BookConverter bookConverter;

    LoggedExceptionFactory exceptionFactory;

    @Override
    public void validate(CatalogEntity entity) {
        var book = (Book)entity;
        var bookStr = bookConverter.bookToString(book);

        checkTitleValidity(book, bookStr);
        checkYearOfPublishedValidity(book, bookStr);
        checkAuthorsValidity(book, bookStr);
        checkGenresValidity(book, bookStr);
    }

    private void checkTitleValidity(Book book, String bookStr) {
        if (!StringUtils.hasText(book.getTitle())) {
            exceptionFactory.logAndThrow("Validation error: Empty title in book: %s".formatted(bookStr),
                                         EntityValidationException.class);
        }
    }

    private void checkYearOfPublishedValidity(Book book, String bookStr) {
        if (book.getYearOfPublished() < 0) {
            exceptionFactory.logAndThrow("Validation error: Negative year of published in book: %s".formatted(bookStr),
                                         EntityValidationException.class);
        }
        if (book.getYearOfPublished() > LocalDateTime.now().getYear()) {
            exceptionFactory.logAndThrow(("Validation error: Value year of published is in future " +
                                          "in book: %s").formatted(bookStr), EntityValidationException.class);
        }
    }

    private void checkAuthorsValidity(Book book, String bookStr) {
        var authors = book.getAuthors();
        if (CollectionUtils.isEmpty(authors)) {
            exceptionFactory.logAndThrow("Validation error: Empty authors collection in book: %s".formatted(bookStr),
                                         EntityValidationException.class);
        }
        if (authors.size() != authors.stream().distinct().count()) {
            exceptionFactory.logAndThrow("Validation error: Duplicated author(s) found in book: %s".formatted(bookStr),
                                         EntityValidationException.class);
        }
    }

    private void checkGenresValidity(Book book, String bookStr) {
        var genres = book.getGenres();
        if (CollectionUtils.isEmpty(genres)) {
            exceptionFactory.logAndThrow("Validation error: Empty genres collection in book: %s".formatted(bookStr),
                                         EntityValidationException.class);
        }
        if (genres.size() != genres.stream().distinct().count()) {
            exceptionFactory.logAndThrow("Validation error: Duplicated genre(s) found in book: %s".formatted(bookStr),
                                         EntityValidationException.class);
        }
    }
}
