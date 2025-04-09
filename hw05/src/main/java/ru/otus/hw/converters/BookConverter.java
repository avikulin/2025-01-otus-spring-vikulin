package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    public String bookToString(Book book) {
        var authorsString = book.getAuthors().stream()
                .map(authorConverter::authorToString)
                .collect(Collectors.joining(", "));
        var genresString = book.getGenres().stream()
                .map(genreConverter::genreToString)
                .map("%s"::formatted)
                .collect(Collectors.joining(", "));
        return "[%d] %s (%d) %s\tauthors: %s%s\tgenres:  %s%s".formatted(
                book.getId(),
                book.getTitle(),
                book.getYearOfPublished(),
                System.lineSeparator(),
                authorsString,
                System.lineSeparator(),
                genresString,
                System.lineSeparator());
    }
}
