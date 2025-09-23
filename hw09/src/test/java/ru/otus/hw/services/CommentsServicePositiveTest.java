package ru.otus.hw.services;

import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.data.CommentsArgProvider;
import ru.otus.hw.data.TestDataProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.contracts.CommentsService;
import ru.otus.hw.utils.factories.exceptions.LoggedExceptionFactoryImpl;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("Positive tests pack for <Comment> service")

//отключаем транзакции, как сказано в ДЗ, но я правда не понимаю - зачем?
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({CommentServiceImpl.class, CommentConverter.class, TestDataProvider.class, LoggedExceptionFactoryImpl.class})
class CommentsServicePositiveTest extends ConfigurableByPropertiesTestBase {
    @Autowired
    TestDataProvider testDataProvider;

    @Autowired
    CommentsService commentsService;

    @Autowired
    CommentConverter commentConverter;

    @Test
    @DisplayName("Getting the certain comment by it's ID tag")
    void findById() {
        var expected = testDataProvider.getTestComments().get(0);
        var actual = assertDoesNotThrow(()->commentsService.findById(expected.getId()).orElseThrow());
        assertEquals(expected, actual);
        assertDoesNotThrow(()->commentConverter.commentToString(actual));
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Getting list of all the comments, related to the certain book (by book's ID tag)")
    @ArgumentsSource(CommentsArgProvider.class)
    void findAllByBookId(String testName, Book expected) {
        var comments = assertDoesNotThrow(()->commentsService.findAllByBookId(expected.getId()));
        assertIterableEquals(comments, expected.getComments());
        assertDoesNotThrow(()->comments.stream().map(commentConverter::commentToString));
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Inserting a new comment to the existing book")
    @ArgumentsSource(CommentsArgProvider.class)
    void insertNewComment(String testName, Book expected) {
        var bookId = expected.getId();
        var numberOfExistingComments = expected.getComments().size();
        var commentText = "NEW-COMMENT-" + LocalDateTime.now();
        var actualComment = assertDoesNotThrow(() -> commentsService.insert(bookId, commentText));
        assertEquals(bookId, actualComment.getBookId());
        assertEquals(commentText, actualComment.getText());
        assertDoesNotThrow(()-> commentConverter.commentToString(actualComment));
        var actualComments = commentsService.findAllByBookId(bookId);
        assertEquals(numberOfExistingComments + 1, actualComments.size());
    }

    @Test
    @DisplayName("Updating the existing comment by it's ID tag")
    void updateCommentText() {
        var expected = testDataProvider.getTestComments().get(0);
        var commentId = expected.getId();
        var bookId = expected.getBookId();
        var newText = expected.getText() + "-NEW-" + LocalDateTime.now();
        var actual = assertDoesNotThrow(() -> commentsService.update(commentId, newText));
        assertEquals(commentId, actual.getId());
        assertEquals(bookId, actual.getBookId());
        assertEquals(newText, actual.getText());
        assertDoesNotThrow(()->commentConverter.commentToString(actual));
    }

    @Test
    @DisplayName("Deleting the certain comment from DB by it's ID tag")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void deleteById() {
        var IdOfTheBookWithTwoComments = 3L;
        var IdOfTheFirstComment = 2L;
        var IdOfTheSecondComment = 3L;
        var commentsBeforeDelete = commentsService.findAllByBookId(IdOfTheBookWithTwoComments);
        var expectedCommentIds = Set.of(IdOfTheFirstComment, IdOfTheSecondComment);
        var actualCommentIds = commentsBeforeDelete.stream().map(Comment::getId).collect(Collectors.toSet());
        assertTrue(actualCommentIds.containsAll(expectedCommentIds) &&
                expectedCommentIds.containsAll(actualCommentIds));
        assertDoesNotThrow(()-> commentsService.deleteById(IdOfTheFirstComment));
        var commentsAfterDelete = commentsService.findAllByBookId(IdOfTheBookWithTwoComments);
        assertEquals(1, commentsAfterDelete.size());
        assertEquals(IdOfTheSecondComment, commentsAfterDelete.get(0).getId());
    }
}