package ru.otus.hw.repositories;

import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.data.CommentsArgProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.utils.factories.exceptions.LoggedExceptionFactoryImpl;
import ru.otus.hw.utils.BookCheckerImpl;
import ru.otus.hw.utils.contracts.BookChecker;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("Positive tests pack for <Comment> repository")
@Import({CommentConverter.class, BookCheckerImpl.class, LoggedExceptionFactoryImpl.class})
class CommentRepositoryPositiveTest extends ConfigurableByPropertiesTestBase {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    BookChecker bookChecker;

    @Autowired
    TestEntityManager testEntityManager;

    @ParameterizedTest(name="{0}")
    @DisplayName("Finding all the comments of a certain book by it's ID tag")
    @ArgumentsSource(CommentsArgProvider.class)
    void findAllByBookId(String testName, Book expected) {
        var comments = commentRepository.findAllByBookId(expected.getId());
        var distinct = comments.stream().distinct().toList();
        assertEquals(distinct.size(), comments.size());
        assertIterableEquals(expected.getComments(), comments);
    }

    @ParameterizedTest(name="{0}")
    @DisplayName("Finding the certain comment by it's ID tag")
    @ArgumentsSource(CommentsArgProvider.class)
    void findById(String testName, Book expected) {
        expected.getComments().forEach(c->{
            var comment = commentRepository.findByIdWithCheck(c.getId()).orElseThrow();
            assertEquals(c.getBookId(), comment.getBookId());
            assertEquals(c.getText(), comment.getText());
        });
    }

    @ParameterizedTest(name="{0}")
    @DisplayName("Finding a certain subset of comments by their ID tags")
    @ArgumentsSource(CommentsArgProvider.class)
    void findAllByIds(String testName, Book expected) {
        var commentIds = expected.getComments().stream().map(Comment::getId).collect(Collectors.toSet());
        var comments = new HashSet<>(commentRepository.findAllByIds(commentIds));
        assertTrue(expected.getComments().containsAll(comments));
        assertTrue(comments.containsAll(expected.getComments()));
    }

    @Test
    @DisplayName("Updating the comment with new text value")
    void updateCommentText() {
        var comment = commentRepository.findByIdWithCheck(1L).orElseThrow();
        comment.setText(comment.getText()+"-new");
        commentRepository.save(comment);
        testEntityManager.flush(); //принудительно проталкиваем в БД
        var book = testEntityManager.find(Book.class, comment.getBookId());
        var newText = book.getComments()
                          .stream()
                          .filter(c->c.getId() == comment.getId())
                          .findFirst()
                          .orElseThrow()
                          .getText();
        assertEquals(comment.getText(), newText);
        assertEquals(book.getComments().size(), this.bookChecker.getCommentsCount(book.getId()));
        var expectedKeys = book.getComments()
                               .stream()
                               .map(Comment::getId)
                               .sorted()
                               .map(String::valueOf)
                               .collect(Collectors.joining(","));
        assertEquals(expectedKeys, this.bookChecker.getCommentsKeys(book.getId()));
    }

    @Test
    @DisplayName("Updating the comment's book ID with new value")
    void moveCommentToAnotherBook() {
        var comment = commentRepository.findById(3L).orElseThrow();
        var oldBookId = comment.getBookId(); // книга №3 с двумя комментами
        comment.setBookId(1L); //книга без комментов
        commentRepository.save(comment);
        testEntityManager.flush(); //принудительно проталкиваем в БД
        // комплекс проверок для книги, в которую перенесли коммент
        var bookWithNewComment = testEntityManager.find(Book.class, comment.getBookId());
        assertEquals(1L, bookWithNewComment.getComments().size());
        assertEquals(1L, this.bookChecker.getCommentsCount(comment.getBookId()));
        var newCommentText = bookWithNewComment.getComments()
                .stream()
                .filter(c->c.getId() == comment.getId())
                .findFirst()
                .orElseThrow()
                .getText();
        assertEquals(comment.getText(), newCommentText);
        // комплекс проверок для книги, из которой перенесли коммент
        var bookWithoutOneComment = testEntityManager.find(Book.class, oldBookId);
        var expectedCommentCount = bookWithoutOneComment.getComments()
                                                        .stream()
                                                        .filter(c->c.getId() != comment.getId())
                                                        .count();
        var expectedCommentKeys = bookWithoutOneComment.getComments()
                                                       .stream()
                                                       .map(Comment::getId)
                                                       .filter(id -> id != comment.getId())
                                                       .sorted().map(String::valueOf)
                                                       .collect(Collectors.joining(","));
        assertEquals(1L, expectedCommentCount);
        assertEquals(expectedCommentKeys, this.bookChecker.getCommentsKeys(bookWithoutOneComment.getId()));
        assertFalse(bookWithoutOneComment.getComments().contains(comment));
    }


    @ParameterizedTest(name="{0}")
    @DisplayName("Adding the brand-new comment for book")
    @ArgumentsSource(CommentsArgProvider.class)
    void insertNewComment(String testName, Book expected) {
        var bookId = expected.getId();
        var commentsCount = expected.getComments().size();
        assertEquals(commentsCount, this.bookChecker.getCommentsCount(bookId));

        var comment = new Comment(bookId, "COMMENT-NEW-" + LocalDateTime.now());
        assertFalse(expected.getComments().contains(comment));

        this.commentRepository.save(comment);
        this.testEntityManager.flush();  //принудительно проталкиваем в БД

        var book = this.testEntityManager.find(Book.class, bookId);
        assertTrue(book.getComments().contains(comment));
        assertEquals(commentsCount + 1, book.getComments().size());
        var expectedCommentKeys = book.getComments()
                                      .stream()
                                      .map(Comment::getId)
                                      .sorted().map(String::valueOf)
                                      .collect(Collectors.joining(","));
        assertEquals(expectedCommentKeys, this.bookChecker.getCommentsKeys(bookId));
    }

    @Test
    @DisplayName("Deleting the certain comment by it's ID tag")
    void deleteById() {
        var comment = this.testEntityManager.find(Comment.class, 3L);
        var bookId = comment.getBookId();
        var book = this.testEntityManager.find(Book.class, bookId);
        var commentCount = book.getComments().size();

        this.commentRepository.deleteById(comment.getId());
        this.testEntityManager.flush();  //принудительно проталкиваем в БД

        testEntityManager.getEntityManager().refresh(book);
        assertFalse(book.getComments().contains(comment));
        assertEquals(commentCount - 1, this.bookChecker.getCommentsCount(bookId));
        var expectedCommentKeys = book.getComments()
                .stream()
                .map(Comment::getId)
                .sorted().map(String::valueOf)
                .collect(Collectors.joining(","));
        assertEquals(expectedCommentKeys, this.bookChecker.getCommentsKeys(bookId));
    }
}