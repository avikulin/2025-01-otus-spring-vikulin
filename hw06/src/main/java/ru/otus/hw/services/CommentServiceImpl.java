package ru.otus.hw.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.contracts.CommentRepository;
import ru.otus.hw.services.contracts.CommentsService;
import ru.otus.hw.utils.factories.exceptions.contracts.LoggedExceptionFactory;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentsService {

    CommentRepository commentRepository;

    LoggedExceptionFactory exceptionFactory;

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(long id) {
        if (id < 1) {
            exceptionFactory.logAndThrow("Trying to process impossible value as ID for find comment operation",
                                              EntityValidationException.class);
        }
        return this.commentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findAllByBookId(long bookId) {
        if (bookId < 1) {
            exceptionFactory.logAndThrow("Trying to process impossible value as book's ID " +
                                              "for find all comments operation", EntityValidationException.class);
        }
        return this.commentRepository.findAllByBookId(bookId);
    }


    @Override
    @Transactional
    public Comment insert(long bookId, String commentText) {
        if (bookId < 1L) {
            exceptionFactory.logAndThrow("Book's ID must be greater than <0>", EntityValidationException.class);
        }
        if (StringUtils.isBlank(commentText)) {
            exceptionFactory.logAndThrow("Comment's text must not be blank", EntityValidationException.class);
        }
        var comment = new Comment(bookId, commentText);
        return this.commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment update(long id, String commentText) {
        if (id < 1L) {
            exceptionFactory.logAndThrow("Comment's ID must be greater than <0>", EntityValidationException.class);
        }
        if (StringUtils.isBlank(commentText)) {
            exceptionFactory.logAndThrow("Comment's text must not be blank", EntityValidationException.class);
        }
        var comment = this.commentRepository.findById(id).orElseThrow();
        comment.setText(commentText);
        return this.commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        if (id < 1) {
            exceptionFactory.logAndThrow("Trying to process impossible value as ID for delete comment operation",
                                              EntityValidationException.class);
        }
        this.commentRepository.deleteById(id);
    }
}
