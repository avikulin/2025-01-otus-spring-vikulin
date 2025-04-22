package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.MoreThanOneEntityFound;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.contracts.AuthorRepository;
import ru.otus.hw.utils.factories.exceptions.contracts.LoggedExceptionFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaAuthorRepository implements AuthorRepository {

    @PersistenceContext
    EntityManager entityManager;

    LoggedExceptionFactory exceptionFactory;

    @Override
    public List<Author> findAll() {
        List<Author> result = null;
        try {
            var query = entityManager.createQuery("""
                                                    SELECT
                                                        a
                                                    FROM
                                                        Author a""", Author.class);
            result = query.getResultList();
        } catch (PersistenceException e) {
            exceptionFactory.logAndThrow("Something went wrong with access to DB while finding authors",
                                              AppInfrastructureException.class);
        }
        return result;
    }

    @Override
    public Optional<Author> findById(long id) {
        Author result = null;
        try {
            var query = entityManager.createQuery("""
                                                    SELECT
                                                        a
                                                    FROM
                                                        Author a
                                                    WHERE
                                                        a.id = :id""", Author.class);
            query.setParameter("id", id);
            result = query.getSingleResult();
        } catch (NoResultException e) {
            exceptionFactory.logAndThrow("No author found with id = %d", id, EntityNotFoundException.class, e);
        } catch (NonUniqueResultException e) {
            exceptionFactory.logAndThrow("More than one author found with id = %d",
                                              id, MoreThanOneEntityFound.class, e);
        } catch (PersistenceException e) {
            exceptionFactory.logAndThrow("Something went wrong with access to DB " +
                                              "while finding authors", AppInfrastructureException.class, e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Author> findAllByIds(Set<Long> ids) {
        List<Author> result = null;
        try {
            var query = entityManager.createQuery("""
                                                    SELECT
                                                        a
                                                    FROM
                                                        Author a
                                                    WHERE a.id IN (:ids)""", Author.class);
            query.setParameter("ids", ids);
            result = query.getResultList();
        } catch (PersistenceException e) {
            var idStr = ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
            exceptionFactory.logAndThrow(("Something went wrong with access to DB " +
                                          "while finding authors by ids: [%s]").formatted(idStr),
                                          AppInfrastructureException.class, e);
        }
        return result;
    }
}
