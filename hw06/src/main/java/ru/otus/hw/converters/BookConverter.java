package ru.otus.hw.converters;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookConverter {
    AuthorConverter authorConverter;

    GenreConverter genreConverter;

    CommentConverter commentConverter;

    public String bookToString(Book book) {
        var authors = book.getAuthors();
        var genres  = book.getGenres();
        var comments = book.getComments();
        var authorsString = authors == null ? "" : authors.stream()
                                                          .map(authorConverter::authorToString)
                                                          .collect(Collectors.joining(", "));
        var genresString = genres == null ? ""   :  genres.stream()
                                                          .map(genreConverter::genreToString)
                                                          .collect(Collectors.joining(", "));
        var commentsString = comments == null ? "" : comments.stream()
                                                          .map(commentConverter::commentToString)
                                                          .map("\t\t%s"::formatted)
                                                          .collect(Collectors.joining(System.lineSeparator(), System.lineSeparator(), System.lineSeparator()));
        return "[%d] %s (%d) %s\tauthors: %s%s\tgenres:  %s%s\tcomments: %s"
                .formatted(
                    book.getId(),
                    book.getTitle(),
                    book.getYearOfPublished(),
                    System.lineSeparator(),
                    authorsString,
                    System.lineSeparator(),
                    genresString,
                    System.lineSeparator(),
                    commentsString
                );
    }
}
