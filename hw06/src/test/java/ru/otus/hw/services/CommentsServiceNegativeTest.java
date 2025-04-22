package ru.otus.hw.services;

import base.ConfigurableByPropertiesTestBase;
import data.CommentsArgProvider;
import data.TestDataProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.JpaCommentRepository;
import ru.otus.hw.services.contracts.CommentsService;
import ru.otus.hw.utils.factories.exceptions.LoggedExceptionFactoryImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("Negative tests pack for <Comment> service")

//отключаем транзакции, как сказано в ДЗ, но я правда не понимаю - зачем?
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({JpaCommentRepository.class, CommentServiceImpl.class, CommentConverter.class, TestDataProvider.class,
         LoggedExceptionFactoryImpl.class})
class CommentsServiceNegativeTest extends ConfigurableByPropertiesTestBase {
    @Autowired
    TestDataProvider testDataProvider;

    @Autowired
    CommentsService commentsService;

    @Test
    @DisplayName("Throws on trying to find by not existing ID tag")
    void throwsOnFindingByNotExistingId() {
        assertThrows(EntityNotFoundException.class, ()-> commentsService.findById(555L));
    }

    @Test
    @DisplayName("Throws on trying to find by impossible ID value")
    void throwsOnFindingByImpossibleId() {
        assertThrows(EntityValidationException.class, ()-> commentsService.findById(0L));
    }

    @Test
    @DisplayName("Return an empty collection on find by not existing book's ID tag")
    void returnsEmptyCollectionForBookIdWhichNotExists() {
        assertTrue(commentsService.findAllByBookId(555L).isEmpty());
    }

    @Test
    @DisplayName("Throws on trying to find by impossible book's ID value")
    void throwsOnFindingByImpossibleBookId() {
        assertThrows(EntityValidationException.class, ()-> commentsService.findAllByBookId(0L));
    }


    @Test
    @DisplayName("Throws on updating the text to null")
    void throwsOnUpdatingTitleWithNull() {
        var comment = testDataProvider.getTestComments().get(0);
        assertThrows(
                NullPointerException.class,
                ()-> comment.setText(null)
        );
    }

    @Test
    @DisplayName("Throws on updating the text to empty string")
    void throwsOnUpdatingTitleWithEmptyString() {
        assertThrows(
                EntityValidationException.class,
                ()-> commentsService.update(1L, "")
        );
    }

    @Test
    @DisplayName("Throws on updating the text to blank string")
    void throwsOnUpdatingTitleWithBlankString() {
        assertThrows(
                EntityValidationException.class,
                ()-> commentsService.update(1L, "        ")
        );
    }

    @DisplayName("Throws on inserting the book without title (empty string)")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(CommentsArgProvider.class)
    void throwsOnInsertingWithEmptyTitle(String testName, Book expected) {
        assertThrows(
                EntityValidationException.class,
                ()-> commentsService.insert(expected.getId(), "")
        );
    }

    @DisplayName("Throws on inserting the book without title (blank string)")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(CommentsArgProvider.class)
    void throwsOnInsertingWithTitleWithBlankString(String testName, Book expected) {
        assertThrows(
                EntityValidationException.class,
                ()-> commentsService.insert(expected.getId(), "       ")
        );
    }

    @Test
    @DisplayName("Throws on trying to delete by not existing ID tag")
    void throwsOnDeletingByNotExistingId() {
        assertThrows(EntityNotFoundException.class, ()-> commentsService.deleteById(555L));
    }

    @Test
    @DisplayName("Throws on trying to delete by impossible ID value")
    void throwsOnDeletingByImpossibleId() {
        assertThrows(EntityValidationException.class, ()-> commentsService.deleteById(0L));
    }
}