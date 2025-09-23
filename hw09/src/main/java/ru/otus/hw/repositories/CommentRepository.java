package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.base.BaseCommentRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends BaseCommentRepository {

    default List<Comment> findAllByBookIdWithCheck(long bookId) {
        if (bookId < 1) {
            throw new EntityValidationException("Trying to process impossible value as book's ID for find operation");
        }
        return findAllByBookId(bookId);
    }

    default Optional<Comment> findByIdWithCheck(long id) {
        if (id < 1) {
            throw new EntityValidationException("Trying to process impossible value as ID for find operation");
        }
        var item = this.findById(id);
        if (item.isEmpty()) {
            throw new EntityNotFoundException("The comment with ID = %d is not found in the DB".formatted(id));
        }
        return item;
    }

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :id")
    int deleteByIdWithCheck(@Param("id") Long id);

    default void deleteById(Long id) {
        if (id < 1) {
            throw new EntityValidationException("Trying to process impossible value as ID for delete operation");
        }
        var rowsAffected = this.deleteByIdWithCheck(id);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("The comment with ID = %d is not found in the DB".formatted(id));
        }
    }
}
