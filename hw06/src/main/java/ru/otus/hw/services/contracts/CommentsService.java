package ru.otus.hw.services.contracts;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentsService {
    Optional<Comment> findById(long id);

    List<Comment> findAllByBookId(long bookId);

    Comment insert(long bookId, String commentText);

    Comment update(long id, String commentText);

    void deleteById(long id);
}
