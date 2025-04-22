package ru.otus.hw.repositories;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.exceptions.MoreThanOneEntityFound;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.contracts.BookRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        if (id < 1) {
            log.error("Trying to process impossible value as ID for find operation");
            throw new EntityValidationException("The ID must be 1 or greater");
        }
        try {
            // сохраняем преемственность по отношению к предыдущему ДЗ: там единичный агрегат получали через JOIN
            var query = this.entityManager.createQuery("""
                                                        SELECT
                                                            b
                                                        FROM
                                                            Book b LEFT JOIN FETCH b.authors
                                                                   LEFT JOIN FETCH b.genres
                                                                   LEFT JOIN FETCH b.comments
                                                        WHERE
                                                            b.id = :id""", Book.class);
            query.setParameter("id", id);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e){
            var msg = "No book found with id = %d".formatted(id);
            log.error(msg, e);
            throw new EntityNotFoundException(msg, e);
        } catch (NonUniqueResultException e){
            var msg = "More than one book found with id = %d".formatted(id);
            log.error(msg, e);
            throw new MoreThanOneEntityFound(msg);
        } catch (PersistenceException e) {
            var msg = "Something went wrong with access to DB while finding book";
            log.error(msg, e);
            throw new AppInfrastructureException(msg);
        }
    }

    @Override
    public List<Book> findAll() {
        try {
            // сохраняем преемственность по отношению к предыдущему ДЗ:
            // там коллекцию агрегатов получали через композицию отдельных запросов
            var fetchGraph = this.entityManager.getEntityGraph("book-aggregate");
            var query = this.entityManager.createQuery("""
                                                        SELECT
                                                            b
                                                        FROM
                                                            Book b
                                                        """, Book.class);
            var books = query.getResultList();
            query.setHint("jakarta.persistence.fetchgraph", fetchGraph);
            if (!books.isEmpty()) { // заставляем инициализировать вложенные коллекции
                books.get(0).getAuthors().size();
                books.get(0).getGenres().size();
                books.get(0).getComments().size();
            }
            return books;
        } catch (PersistenceException e) {
            var msg = "Something went wrong with access to DB while finding genres";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            this.entityManager.persist(book);
        } else {
            this.entityManager.merge(book);
        }
        return book;
    }

    @Override
    public void deleteById(long id) {
        var book = this.findById(id); // обработка исключения уже есть внутри
        this.entityManager.remove(book);
    }
}
