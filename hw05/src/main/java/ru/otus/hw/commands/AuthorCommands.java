package ru.otus.hw.commands;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.services.contracts.AuthorService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorCommands {

    AuthorService authorService;

    AuthorConverter authorConverter;

    @ShellMethod(value = "List all authors", key = "ls-author")
    public String listAllAuthors() {
        return authorService.findAll().stream()
                .map(authorConverter::authorToString)
                .collect(Collectors.joining(System.lineSeparator()))
                + System.lineSeparator();
    }

    @ShellMethod(value = "Print author, defined by ID param", key = "get-author")
    public String findAuthorById(Long id) {
        return authorConverter.authorToString(authorService.findById(id))
                + System.lineSeparator();
    }
}
