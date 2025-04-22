package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.exceptions.MoreThanOneEntityFound;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.contracts.BookRepository;
import ru.otus.hw.utils.factories.exceptions.contracts.LoggedExceptionFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    EntityManager entityManager;

    LoggedExceptionFactory exceptionFactory;

    @Override
    // сохраняем преемственность по отношению к предыдущему ДЗ: там единичный агрегат получали через JOIN
    public Optional<Book> findById(long id) {
        if (id < 1) {
            exceptionFactory.logAndThrow("Trying to process impossible value " +
                                              "as ID for find operation", id, EntityValidationException.class);
        }
        Book result = null;
        try {
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
            result = query.getSingleResult();
        } catch (NoResultException e) {
            exceptionFactory.logAndThrow("No book found with id = %d", id, EntityNotFoundException.class, e);
        } catch (NonUniqueResultException e) {
            exceptionFactory.logAndThrow("More than one book found with id = %d", id,
                                              MoreThanOneEntityFound.class, e);
        } catch (PersistenceException e) {
            exceptionFactory.logAndThrow("Something went wrong with access to DB " +
                                              "while finding book", AppInfrastructureException.class, e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    // сохраняем преемственность по отношению к предыдущему ДЗ:
    // там коллекцию агрегатов получали через композицию отдельных запросов
    public List<Book> findAll() {
        List<Book> result = null;
        try {
            var fetchGraph = this.entityManager.getEntityGraph("book-aggregate");
            var query = this.entityManager.createQuery("""
                                                        SELECT
                                                            b
                                                        FROM
                                                            Book b
                                                        """, Book.class);
            result = query.getResultList();
            query.setHint("jakarta.persistence.fetchgraph", fetchGraph);
            if (!result.isEmpty()) { // заставляем инициализировать вложенные коллекции
                result.get(0).getAuthors().size();
                result.get(0).getGenres().size();
                result.get(0).getComments().size();
            }
        } catch (PersistenceException e) {
            exceptionFactory.logAndThrow("Something went wrong with access to DB " +
                                              "while finding genres", AppInfrastructureException.class, e);
        }
        return result;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            synchronizeContext(book, entityManager::persist);
            return book;
        } else {
            var managedEntity = getManagedStateFromDB(book);
            synchronizeContext(managedEntity, entityManager::merge);  // подтягиваем пересобранные коллекции из БД
            return managedEntity;
        }
    }

    private Book getManagedStateFromDB(Book detached) {
        Book managedEntity = this.findById(detached.getId()).orElseThrow();
        // переносим скаляры
        managedEntity.setTitle(detached.getTitle());
        managedEntity.setYearOfPublished(detached.getYearOfPublished());
        // пересобираем авторов
        var authors = managedEntity.getAuthors();
        authors.clear();
        authors.addAll(detached.getAuthors());
        // пересобираем жанры
        var genres = managedEntity.getGenres();
        genres.clear();
        genres.addAll(detached.getGenres());
        // комменты не переносим - это отдельный сервис и отдельный репозиторий
        return managedEntity;
    }

    private void synchronizeContext(Book book, Consumer<Book> func) {
        try {
            func.accept(book);
        } catch (HibernateException e) {
            exceptionFactory.logAndThrow("Something goes wrong during the " +
                                              "updating the entity state from DB", AppInfrastructureException.class, e);
        }
    }

    @Override
    public void deleteById(long id) {
        var book = this.findById(id).orElseThrow(); // обработка исключения уже есть внутри
        book.getComments().clear();
        this.entityManager.remove(book); //комменты подчистит CascadeType.REMOVE
    }
}
