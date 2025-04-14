package ru.otus.hw.utils.validators;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.contracts.CatalogEntity;
import ru.otus.hw.utils.validators.contracts.EntityValidator;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookValidator implements EntityValidator {

    BookConverter bookConverter;
    @Override
    public void validate(CatalogEntity entity) {
        var book = (Book)entity;
        var bookStr = bookConverter.bookToString(book);

        checkTitleValidity(book, bookStr);
        checkYearOfPublishedValidity(book, bookStr);
        checkAuthorsValidity(book, bookStr);
        checkGenresValidity(book, bookStr);
    }

    private static void checkTitleValidity(Book book, String bookStr) {
        if (!StringUtils.hasText(book.getTitle())) {
            log.error("Validation error: Empty title in book: {}", bookStr);
            throw new EntityValidationException("Title cannot be omitted or kept empty");
        }
    }

    private static void checkYearOfPublishedValidity(Book book, String bookStr) {
        if (book.getYearOfPublished() < 0) {
            log.error("Validation error: Negative year of published in book: {}", bookStr);
            throw new EntityValidationException("Year of published cannot be negative");
        }
        if (book.getYearOfPublished() > LocalDateTime.now().getYear()) {
            log.error("Validation error: Value year of published is in future in book: {}", bookStr);
            throw new EntityValidationException("Year of published cannot be in future");
        }
    }

    private static void checkAuthorsValidity(Book book, String bookStr) {
        var authors = book.getAuthors();
        if (CollectionUtils.isEmpty(authors)) {
            log.error("Validation error: Empty authors collection in book: {}", bookStr);
            throw new EntityValidationException("Authors must not be an empty collection");
        }
        if (authors.size() != authors.stream().distinct().count()) {
            log.error("Validation error: Duplicated author(s) found in book: {}", bookStr);
            throw new EntityValidationException("Authors must be unique");
        }
    }

    private static void checkGenresValidity(Book book, String bookStr) {
        var genres = book.getGenres();
        if (CollectionUtils.isEmpty(genres)) {
            log.error("Validation error: Empty genres collection in book: {}", bookStr);
            throw new EntityValidationException("Genres must not be an empty collection");
        }
        if (genres.size() != genres.stream().distinct().count()) {
            log.error("Validation error: Duplicated genre(s) found in book: {}", bookStr);
            throw new EntityValidationException("Authors must be unique");
        }
    }


}
