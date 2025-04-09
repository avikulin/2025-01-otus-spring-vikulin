package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.contracts.GenreService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class GenreCommands {

    private final GenreService genreService;

    private final GenreConverter genreConverter;

    @ShellMethod(value = "List all genres", key = "ls-genre")
    public String listAllGenres() {
        return formatList(genreService.findAll());
    }

    @ShellMethod(value = "List all genres", key = "get-genre")
    public String findAllGenresByIds(Set<Long> ids) {
        return formatList(genreService.findAllByIds(ids));
    }

    private String formatList(List<Genre> genres) {
        return genres.stream()
                .map(genreConverter::genreToString)
                .collect(Collectors.joining(System.lineSeparator()))
                +System.lineSeparator();
    }
}
