package ru.otus.hw.utils.contracts;

import ru.otus.hw.models.Book;

public interface BookChecker {
    boolean areEqual(Book first, Book second);

    boolean areEqualIgnoringComments(Book first, Book second);

    String getAuthorsKeys(long bookId);

    String getGenresKeys(long bookId);

    String getCommentsKeys(long bookId);

    long getCommentsCount(long bookId);
}
