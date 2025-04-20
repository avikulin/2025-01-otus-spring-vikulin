package ru.otus.hw.repositories;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.MoreThanOneEntityFound;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.contracts.GenreRepository;

import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaGenreRepository implements GenreRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Genre> findAll() {
        try {
            var query = this.entityManager.createQuery("""
                                                        SELECT
                                                            g
                                                        FROM
                                                            Genre g""", Genre.class);
            return query.getResultList();
        } catch (PersistenceException e) {
            var msg = "Something went wrong with access to DB while finding genres";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    @Override
    public Genre findById(long id) {
        try {
            var query = this.entityManager.createQuery("""
                                                        SELECT
                                                            g
                                                        FROM
                                                            Genre g
                                                        WHERE
                                                            g.id = :id""", Genre.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e){
            var msg = "No genre found with id = %d".formatted(id);
            log.error(msg, e);
            throw new EntityNotFoundException(msg, e);
        } catch (MoreThanOneEntityFound e){
            var msg = "More than one genre found with id = %d".formatted(id);
            log.error(msg, e);
            throw new MoreThanOneEntityFound(msg);
        } catch (PersistenceException e) {
            var msg = "Something went wrong with access to DB while finding authors";
            log.error(msg, e);
            throw new AppInfrastructureException(msg);
        }
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        try {
            var query = this.entityManager.createQuery("""
                                                        SELECT
                                                            g
                                                        FROM
                                                            Genre g
                                                        WHERE
                                                            g.id in (:ids)""", Genre.class);
            query.setParameter("ids", ids);
            return query.getResultList();
        } catch (PersistenceException e) {
            var msg = "Something went wrong with access to DB while finding genres";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }
}
