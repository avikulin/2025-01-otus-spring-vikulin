package ru.otus.hw.repositories.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.otus.hw.models.relations.BookAuthorRelation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LnkBooksAuthorsRowMapper implements RowMapper<BookAuthorRelation> {
    @Override
    public BookAuthorRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
        var bookId = rs.getLong("BOOK_ID");
        var authorId = rs.getLong("AUTHOR_ID");
        return new BookAuthorRelation(bookId, authorId);
    }
}
