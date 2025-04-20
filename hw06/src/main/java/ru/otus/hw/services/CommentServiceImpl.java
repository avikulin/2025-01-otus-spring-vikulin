package ru.otus.hw.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.contracts.CommentRepository;
import ru.otus.hw.services.contracts.CommentsService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentsService {

    private final CommentRepository commentRepository;

    @Override
    public Optional<Comment> findById(long id) {
        return this.commentRepository.findById(id);
    }

    @Override
    public List<Comment> findAllByBookId(long bookId) {
        return this.commentRepository.findAllByBookId(bookId);
    }

    @Transactional
    @Override
    public Comment insert(long bookId, String commentText) {
        Validate.isTrue(bookId > 0L, "Book's ID must be greater than <0>");
        Validate.notBlank(commentText, "commentText must not be blank");
        var comment = new Comment(0L, bookId, commentText);
        return this.commentRepository.save(comment);
    }

    @Transactional
    @Override
    public Comment update(long id, String commentText) {
        Validate.isTrue(id > 0L, "Comment's ID must be greater than <0>");
        Validate.notBlank(commentText, "Comment's text must not be blank/empty");
        var commentItem = this.commentRepository.findById(id);
        Validate.isTrue(commentItem.isPresent(), "Comment with [id] = %d not present in DB".formatted(id));
        var commentObj = commentItem.get();
        commentObj.setText(commentText);
        return this.commentRepository.save(commentObj);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        Validate.isTrue(id > 0L, "Comment's ID must be greater than <0>");
        this.commentRepository.deleteById(id);
    }
}
