package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.MoreThanOneEntityFound;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.contracts.GenreRepository;
import ru.otus.hw.utils.factories.exceptions.contracts.LoggedExceptionFactory;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaGenreRepository implements GenreRepository {

    @PersistenceContext
    EntityManager entityManager;

    LoggedExceptionFactory exceptionFactory;

    @Override
    public List<Genre> findAll() {
        List<Genre> result = null;
        try {
            var query = this.entityManager.createQuery("""
                                                        SELECT
                                                            g
                                                        FROM
                                                            Genre g""", Genre.class);
            result = query.getResultList();
        } catch (PersistenceException e) {
            exceptionFactory.logAndThrow("Something went wrong with access to DB while finding genres",
                                               AppInfrastructureException.class, e);

        }
        return result;
    }

    @Override
    public Genre findById(long id) {
        Genre result = null;
        try {
            var query = this.entityManager.createQuery("""
                                                        SELECT
                                                            g
                                                        FROM
                                                            Genre g
                                                        WHERE
                                                            g.id = :id""", Genre.class);
            query.setParameter("id", id);
            result = query.getSingleResult();
        } catch (NoResultException e) {
            exceptionFactory.logAndThrow("No genre found with id = %d", id, EntityNotFoundException.class, e);
        } catch (MoreThanOneEntityFound e) {
            exceptionFactory.logAndThrow("More than one genre found with id = %d",
                                              id, MoreThanOneEntityFound.class, e);
        } catch (PersistenceException e) {
            exceptionFactory.logAndThrow("Something went wrong with access to DB while finding authors",
                                              AppInfrastructureException.class, e);
        }
        return result;
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        List<Genre> result = null;
        try {
            var query = this.entityManager.createQuery("""
                                                        SELECT
                                                            g
                                                        FROM
                                                            Genre g
                                                        WHERE
                                                            g.id in (:ids)""", Genre.class);
            query.setParameter("ids", ids);
            result = query.getResultList();
        } catch (PersistenceException e) {
            exceptionFactory.logAndThrow("Something went wrong with access to DB while finding genres",
                                              AppInfrastructureException.class, e);
        }
        return result;
    }
}
