package ru.otus.hw.repositories.contracts;

import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentRepository {

    List<Comment> findAllByBookId(long bookId);

    Optional<Comment> findById(long id);

    List<Comment> findAllByIds(Set<Long> ids);

    Comment save(Comment comment);

    void deleteById(long id);
}
