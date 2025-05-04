package ru.otus.hw.repositories.mappers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookResultSetExtractor implements ResultSetExtractor<List<Book>> {

    Book currentBook = null;

    final List<Book> result = new ArrayList<>();

    Long prevBookId = null;

    final Set<Long> authorsKeys = new HashSet<>();

    final Set<Long> genresKeys = new HashSet<>();

    @Override
    public List<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
        while (rs.next()) {
            Long currentBookId = rs.getLong("BOOK_ID");
            if (currentBookId.equals(0L)) {
                continue;
            }
            instantiateBook(rs, currentBookId);
            Long currentAuthorId = rs.getLong("AUTHOR_ID");
            if (!currentAuthorId.equals(0L) && !authorsKeys.contains(currentAuthorId)) {
                appendAuthor(rs, currentAuthorId);
            }
            Long currentGenreId = rs.getLong("GENRE_ID");
            if (!currentGenreId.equals(0L) && !genresKeys.contains(currentGenreId)) {
                appendGenre(rs, currentGenreId);
            }
        }
        if (currentBook != null) {
            result.add(currentBook);
        }
        return result;
    }

    private void instantiateBook(ResultSet rs, Long currentBookId) throws SQLException {
        if (!currentBookId.equals(prevBookId)) {
            var currentBookTitle = rs.getString("BOOK_TITLE");
            var currentBookYearOfPublished = rs.getInt("YEAR_OF_PUBLISHED");
            if (currentBook != null) {
                result.add(currentBook);
            }
            currentBook = createBookWithoutAuthorsAndGenres(currentBookId, currentBookTitle, currentBookYearOfPublished);
            prevBookId = currentBookId;
            authorsKeys.clear();
            genresKeys.clear();
        }
    }

    private Book createBookWithoutAuthorsAndGenres(Long bookId, String bookTitle, int bookYearOfPublished) {
        return new Book(bookId, bookTitle, bookYearOfPublished,
                new ArrayList<>(), new ArrayList<>());
    }

    private void appendAuthor(ResultSet rs, Long currentAuthorId) throws SQLException {
        var currentAuthorName = rs.getString("AUTHOR_FULL_NAME");
        var currentAuthor = new Author(currentAuthorId, currentAuthorName);
        currentBook.getAuthors().add(currentAuthor);
        authorsKeys.add(currentAuthorId);
    }

    private void appendGenre(ResultSet rs, Long currentGenreId) throws SQLException {
        var currentGenreName = rs.getString("GENRE_NAME");
        var currentGenre = new Genre(currentGenreId, currentGenreName);
        currentBook.getGenres().add(currentGenre);
        genresKeys.add(currentGenreId);
    }
}