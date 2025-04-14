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
        var authors = book.getAuthors();
        var genres  = book.getGenres();
        var authorsString = authors == null ? "" : authors.stream()
                                                          .map(authorConverter::authorToString)
                                                          .collect(Collectors.joining(", "));
        var genresString = genres == null ? ""   :  genres.stream()
                                                          .map(genreConverter::genreToString)
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
