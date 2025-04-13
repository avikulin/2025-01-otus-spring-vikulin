package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.services.contracts.BookService;

import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class BookCommands {

    private final BookService bookService;

    private final BookConverter bookConverter;

    @ShellMethod(value = "List all books", key = "ls-book")
    public String findAllBooks() {
        return bookService.findAll().stream()
                .map(bookConverter::bookToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Find book by ID", key = "get-book")
    public String findBookById(long id) {
        return bookService.findById(id)
                .map(bookConverter::bookToString)
                .orElse("Book with id %d not found".formatted(id));
    }

    // bins newBook 1 1,6
    @ShellMethod(value = "Insert book", key = "add-book")
    public String insertBook(String title,  int yearOfPublished, Set<Long> authorIds, Set<Long> genresIds) {
        var savedBook = bookService.insert(title,  yearOfPublished, authorIds, genresIds);
        return bookConverter.bookToString(savedBook);
    }

    // bupd 4 editedBook 3 2,5
    @ShellMethod(value = "Update book", key = "upd-book")
    public String updateBook(long id, String title,  int yearOfPublished, Set<Long> authorIds, Set<Long> genresIds) {
        var savedBook = bookService.update(id, title,  yearOfPublished, authorIds, genresIds);
        return bookConverter.bookToString(savedBook);
    }

    // bdel 4
    @ShellMethod(value = "Delete book by id", key = "rm-book")
    public void deleteBook(long id) {
        bookService.deleteById(id);
    }
}
