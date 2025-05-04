package ru.otus.hw.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.utils.contracts.BookChecker;

import java.util.Map;

@Component
public class BookCheckerImpl implements BookChecker {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Override public boolean areEqual(Book first, Book second){
        var commentsAreEqual = first.getComments().containsAll(second.getComments()) &&
                                second.getComments().containsAll(first.getComments());
        return this.areEqualIgnoringComments(first, second) && commentsAreEqual;
    }

    @Override
    public boolean areEqualIgnoringComments(Book first, Book second) {
        var titleIsEqual = first.getTitle().equals(second.getTitle());
        var yearOfPublishedIsEqual = first.getYearOfPublished() == second.getYearOfPublished();
        var authorsAreEqual = first.getAuthors().containsAll(second.getAuthors()) &&
                second.getAuthors().containsAll(first.getAuthors());
        var genresAreEqual = first.getGenres().containsAll(second.getGenres()) &&
                second.getGenres().containsAll(first.getGenres());
        return titleIsEqual && yearOfPublishedIsEqual && authorsAreEqual && genresAreEqual;
    }

    @Override public String getAuthorsKeys(long bookId){
        return this.jdbcTemplate.queryForObject("""
                SELECT COALESCE(
                           (SELECT
                                LISTAGG(CAST(t.AUTHOR_ID AS VARCHAR),',')
                            FROM
                                (SELECT
                                    BOOK_ID,
                                    AUTHOR_ID
                                FROM
                                    OTUS_HW_06_TEST.LNK_BOOKS_AUTHORS
                                WHERE
                                    BOOK_ID = :bookId
                                ORDER BY
                                    AUTHOR_ID) AS t
                            GROUP BY
                                t.BOOK_ID),
                '')
                """, Map.of("bookId", bookId), String.class);
    }

    @Override public String getGenresKeys(long bookId){
        return this.jdbcTemplate.queryForObject("""
                SELECT COALESCE(
                           (SELECT
                                LISTAGG(CAST(t.GENRE_ID AS VARCHAR),',')
                            FROM
                                (SELECT
                                    BOOK_ID,
                                    GENRE_ID
                                FROM
                                    OTUS_HW_06_TEST.LNK_BOOKS_GENRES
                                WHERE
                                    BOOK_ID = :bookId
                                ORDER BY
                                    GENRE_ID) AS t
                            GROUP BY
                                t.BOOK_ID),
                '')
                """, Map.of("bookId", bookId), String.class);
    }

    @Override public String getCommentsKeys(long bookId){
        return this.jdbcTemplate.queryForObject("""
                SELECT COALESCE(
                           (SELECT
                                LISTAGG(CAST(t.ID AS VARCHAR),',')
                            FROM
                                (SELECT
                                    BOOK_ID,
                                    ID
                                FROM
                                    OTUS_HW_06_TEST.COMMENTS
                                WHERE
                                    BOOK_ID = :bookId
                                ORDER BY
                                    ID) AS t
                            GROUP BY
                                t.BOOK_ID),
                '')
                """, Map.of("bookId", bookId), String.class);
    }

    @Override public long getCommentsCount(long bookId){
        var res =  this.jdbcTemplate.queryForObject("""
                SELECT
                    COALESCE(COUNT(c.ID), 0)
                FROM
                    OTUS_HW_06_TEST.COMMENTS c
                WHERE
                    c.BOOK_ID = :bookId
                """, Map.of("bookId", bookId), Long.class);
        return res == null ? 0 : res; //вроде бы избыточно, но IntelliJ очень хотела....
    }
}
