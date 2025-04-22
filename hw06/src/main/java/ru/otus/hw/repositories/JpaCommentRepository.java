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
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.contracts.CommentRepository;
import ru.otus.hw.utils.factories.exceptions.contracts.LoggedExceptionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    EntityManager entityManager;

    LoggedExceptionFactory exceptionFactory;

    @Override
    public List<Comment> findAllByBookId(long bookId) {
        if (bookId < 1) {
            exceptionFactory.logAndThrow("Trying to process impossible value as book's ID for find operation",
                                              EntityValidationException.class);
        }
        List<Comment> result = null;
        try {
            var query = entityManager.createQuery("""
                                                    SELECT
                                                        c
                                                    FROM
                                                        Comment c
                                                    WHERE
                                                        c.bookId = :bookId""", Comment.class);
            query.setParameter("bookId", bookId);
            result = query.getResultList();
        } catch (PersistenceException e) {
            exceptionFactory.logAndThrow("Something went wrong with access to DB " +
                                              "while finding comments", AppInfrastructureException.class, e);
        }
        return result;
    }

    @Override
    public Optional<Comment> findById(long id) {
        if (id < 1) {
            exceptionFactory.logAndThrow("Trying to process impossible value as book's ID " +
                                              "for find operation", EntityValidationException.class);
        }
        Comment result = null;
        try {
            var query = entityManager.createQuery("""
                                                     SELECT
                                                         c
                                                     FROM
                                                         Comment c
                                                     WHERE
                                                         c.id = :id
                                                     """, Comment.class);
            query.setParameter("id", id);
            result = query.getSingleResult();
        } catch (NoResultException e) {
            exceptionFactory.logAndThrow("No comment found with id = %d", id, EntityNotFoundException.class, e);
        } catch (NonUniqueResultException e) {
            exceptionFactory.logAndThrow("More than one comment found with id = %d",
                                              id, MoreThanOneEntityFound.class, e);
        } catch (PersistenceException e) {
            exceptionFactory.logAndThrow("Something went wrong with access to DB " +
                                              "while finding comment", AppInfrastructureException.class, e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Comment> findAllByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        if (ids.stream().anyMatch(i -> i < 1)) {
            exceptionFactory.logAndThrow("Trying to process impossible value " +
                                              "as book's ID for find operation", EntityValidationException.class);
        }
        List<Comment> result = null;
        try {
            var query = entityManager.createQuery("""
                                                    SELECT
                                                        c
                                                    FROM
                                                        Comment c
                                                    WHERE
                                                        c.id IN (:ids)""", Comment.class);
            query.setParameter("ids", ids);
            result = query.getResultList();
        } catch (PersistenceException e) {
            exceptionFactory.logAndThrow("Something went wrong with access to DB " +
                                              "while finding comments", AppInfrastructureException.class, e);
        }
        return  result;
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getBookId() < 1) {
            exceptionFactory.logAndThrow("Trying to process impossible value as book's ID",
                                              EntityValidationException.class);
        }
        try {
            if (comment.getId() == 0) {
                this.entityManager.persist(comment);
            } else {
                this.entityManager.merge(comment);
            }
        } catch (HibernateException e) {
            exceptionFactory.logAndThrow("Something goes wrong during the DB interoperation",
                                              AppInfrastructureException.class, e);
        }
        return comment;
    }

    @Override
    public void deleteById(long id) {
        var comment = this.findById(id).orElseThrow();
        this.entityManager.remove(comment);
    }
}
