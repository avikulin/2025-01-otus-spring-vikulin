package ru.otus.hw.repositories.base;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BaseCommentRepository extends Repository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.bookId = :bookId")
    List<Comment> findAllByBookId(@Param("bookId") long bookId);

    Optional<Comment> findById(long id);

    @Query("SELECT c FROM Comment c WHERE c.id IN (:ids)")
    List<Comment> findAllByIds(@Param("ids") Set<Long> ids);

    Comment save(Comment comment);

}
