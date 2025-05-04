package ru.otus.hw.repositories.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.otus.hw.models.relations.BookGenreRelation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LnkBooksGenresRowMapper implements RowMapper<BookGenreRelation> {
    @Override
    public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
        var bookId = rs.getLong("BOOK_ID");
        var genreId = rs.getLong("GENRE_ID");
        return new BookGenreRelation(bookId, genreId);
    }
}
