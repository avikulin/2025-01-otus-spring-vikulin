package ru.otus.hw.repositories.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.otus.hw.models.Book;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        var bookId = rs.getLong("ID");
        var bookTitle = rs.getString("TITLE");
        var bookYear = rs.getInt("YEAR_OF_PUBLISHED");
        return new Book(bookId, bookTitle, bookYear, new ArrayList<>(), new ArrayList<>());
    }
}
