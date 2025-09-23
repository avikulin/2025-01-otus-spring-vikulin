package ru.otus.hw.converters;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.Comparator;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookConverter {
    static String MSG_NO_AUTHORS = "(no authors mentioned)";
    static String MSG_NO_GENRES = "(no genres mentioned)";
    static String MSG_NO_COMMENTS = "(there are no comments registered)";

    AuthorConverter authorConverter;

    GenreConverter genreConverter;

    CommentConverter commentConverter;

    public String bookToString(Book book) {
        return "[%d] %s (%d) %s\tauthors: %s%s\tgenres:  %s%s\tcomments: %s"
                .formatted(
                    book.getId(),
                    book.getTitle(),
                    book.getYearOfPublished(),
                    System.lineSeparator(),
                    collectAuthorsString(book),
                    System.lineSeparator(),
                    collectGenresString(book),
                    System.lineSeparator(),
                    collectCommentsString(book)
                );
    }

    private String collectAuthorsString(Book book) {
        var authors = book.getAuthors();
        var authorsAreEmpty = CollectionUtils.isEmpty(authors);
        var authorsString = authors.stream()
                                   .sorted(Comparator.comparingLong(Author::getId))
                                   .map(authorConverter::authorToString)
                                   .collect(Collectors.joining(", "));
        return authorsAreEmpty ? MSG_NO_AUTHORS : authorsString;
    }

    private String collectGenresString(Book book) {
        var genres  = book.getGenres();
        var genresAreEmpty = CollectionUtils.isEmpty(genres);
        var genresString = genres.stream()
                                 .sorted(Comparator.comparingLong(Genre::getId))
                                 .map(genreConverter::genreToString)
                                 .collect(Collectors.joining(", "));
        return genresAreEmpty ? MSG_NO_GENRES : genresString;
    }

    private String collectCommentsString(Book book) {
        var comments = book.getComments();
        var commentsAreEmpty = CollectionUtils.isEmpty(comments);
        var commentsString = comments.stream()
                                     .sorted(Comparator.comparingLong(Comment::getId))
                                     .map(commentConverter::commentToString)
                                     .map("\t\t%s"::formatted)
                                     .collect(
                                             Collectors.joining(
                                                     System.lineSeparator(),
                                                     System.lineSeparator(),
                                                     System.lineSeparator()
                                             )
                                     );
        return commentsAreEmpty ? MSG_NO_COMMENTS : commentsString;
    }
}
