package ru.otus.hw.repositories;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.MoreThanOneEntityFound;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.contracts.CommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Comment> findAllByBookId(long bookId) {
        try {
            var query = entityManager.createQuery("""
                                                    SELECT
                                                        c
                                                    FROM
                                                        Comment c
                                                    WHERE
                                                        c.bookId = :bookId""", Comment.class);
            query.setParameter("bookId", bookId);
            return query.getResultList();
        } catch (PersistenceException e) {
            var msg = "Something went wrong with access to DB while finding comments";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    @Override
    public Optional<Comment> findById(long id) {
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
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e){
            var msg = "No comment found with id = %d".formatted(id);
            log.error(msg, e);
            throw new EntityNotFoundException(msg, e);
        } catch (NonUniqueResultException e){
            var msg = "More than one comment found with id = %d".formatted(id);
            log.error(msg, e);
            throw new MoreThanOneEntityFound(msg);
        } catch (PersistenceException e) {
            var msg = "Something went wrong with access to DB while finding comment";
            log.error(msg, e);
            throw new AppInfrastructureException(msg);
        }
    }

    @Override
    public List<Comment> findAllByIds(Set<Long> ids) {
        try {
            var query = entityManager.createQuery("""
                                                    SELECT
                                                        c
                                                    FROM
                                                        Comment c
                                                    WHERE
                                                        c.id IN (:ids)""", Comment.class);
            query.setParameter("ids", ids);
            return query.getResultList();
        } catch (PersistenceException e) {
            var msg = "Something went wrong with access to DB while finding comments";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0){
            this.entityManager.persist(comment);
        } else
            this.entityManager.merge(comment);
        return comment;
    }

    @Override
    public void deleteById(long id) {
        var commentItem = this.findById(id);
        Validate.isTrue(commentItem.isPresent(), "No comment found with id = %d".formatted(id));
        var commentObj = commentItem.get();
        this.entityManager.remove(commentObj);
    }
}
