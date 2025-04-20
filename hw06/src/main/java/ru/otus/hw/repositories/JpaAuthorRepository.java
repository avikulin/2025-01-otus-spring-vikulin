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
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.contracts.AuthorRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaAuthorRepository implements AuthorRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Author> findAll() {
        try {
            var query = entityManager.createQuery("""
                                                    SELECT
                                                        a
                                                    FROM
                                                        Author a""", Author.class);
            return query.getResultList();
        } catch (PersistenceException e) {
            var msg = "Something went wrong with access to DB while finding authors";
            log.error(msg, e);
            throw new AppInfrastructureException(msg);
        }
    }

    @Override
    public Optional<Author> findById(long id) {
        try {
            var query = entityManager.createQuery("""
                                                    SELECT
                                                        a
                                                    FROM
                                                        Author a
                                                    WHERE
                                                        a.id = :id""", Author.class);
            query.setParameter("id", id);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e){
            var msg = "No author found with id = %d".formatted(id);
            log.error(msg, e);
            throw new EntityNotFoundException(msg, e);
        } catch (NonUniqueResultException e){
            var msg = "More than one author found with id = %d".formatted(id);
            log.error(msg, e);
            throw new MoreThanOneEntityFound(msg);
        } catch (PersistenceException e) {
            var msg = "Something went wrong with access to DB while finding authors";
            log.error(msg, e);
            throw new AppInfrastructureException(msg);
        }
    }

    @Override
    public List<Author> findAllByIds(Set<Long> ids) {
        try {
            var query = entityManager.createQuery("""
                                                    SELECT
                                                        a
                                                    FROM
                                                        Author a
                                                    WHERE a.id IN (:ids)""", Author.class);
            query.setParameter("ids", ids);
            return query.getResultList();
        } catch (PersistenceException e) {
            var idStr = ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
            var msg = "Something went wrong with access to DB while finding authors by ids: [%s]".formatted(idStr);
            log.error(msg, e);
            throw new AppInfrastructureException(msg);
        }
    }
}
