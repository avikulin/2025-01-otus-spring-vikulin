package ru.otus.hw.utils.contracts;

import ru.otus.hw.models.Book;

public interface DeepCloneUtils {
    Book cloneBook(Book book);

    Book cloneBookAsNew(Book book);
}
