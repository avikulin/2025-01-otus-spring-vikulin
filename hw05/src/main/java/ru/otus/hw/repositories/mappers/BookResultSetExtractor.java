package ru.otus.hw.repositories.mappers;

import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
public class BookResultSetExtractor implements ResultSetExtractor<List<Book>> {

    @Override
    public List<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Book currentBook = null;
        List<Book> result = new ArrayList<>();
        Long prevBookId = null;
        var authorsKeys = new HashSet<>();
        var genresKeys = new HashSet<>();
        while (rs.next()) {
            Long currentBookId = rs.getLong("BOOK_ID");
            if (currentBookId.equals(0L)) {
                continue;
            }
            if (!currentBookId.equals(prevBookId)){
                var currentBookTitle = rs.getString("BOOK_TITLE");
                var currentBookYearOfPublished = rs.getInt("YEAR_OF_PUBLISHED");
                if (currentBook != null){
                    result.add(currentBook);
                }
                currentBook = new Book(currentBookId, currentBookTitle, currentBookYearOfPublished,
                        new ArrayList<>(), new ArrayList<>());
                prevBookId = currentBookId;
                authorsKeys.clear();
                genresKeys.clear();
            }
            Long currentAuthorId = rs.getLong("AUTHOR_ID");
            if (!currentAuthorId.equals(0L)&&!authorsKeys.contains(currentAuthorId)){
                var currentAuthorName = rs.getString("AUTHOR_FULL_NAME");
                var currentAuthor = new Author(currentAuthorId, currentAuthorName);
                currentBook.getAuthors().add(currentAuthor);
                authorsKeys.add(currentAuthorId);
            }
            Long currentGenreId = rs.getLong("GENRE_ID");
            if (!currentGenreId.equals(0L) && !genresKeys.contains(currentGenreId)){
                var currentGenreName = rs.getString("GENRE_NAME");
                var currentGenre = new Genre(currentGenreId, currentGenreName);
                currentBook.getGenres().add(currentGenre);
                genresKeys.add(currentGenreId);
            }
        }
        if (currentBook != null){
            result.add(currentBook);
        }
        return result;
    }
}