package ru.otus.hw.repositories.base;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BaseBookRepository extends Repository<Book, Long> {
    /* к сожалению такая конструкция плохо работает при update-операциях, где она вызывается в т.ч.
    @Query("""
            SELECT
                b
            FROM
                Book b JOIN FETCH b.authors
                       JOIN FETCH b.genres
                       JOIN FETCH b.comments
           WHERE
                b.id = :bookId""")*/
    @EntityGraph("book-aggregate")
    Optional<Book> findById(@Param("bookId") long id);

    @Query("SELECT b FROM Book b")
    List<Book> findAll();

    Book save(Book book);
}
