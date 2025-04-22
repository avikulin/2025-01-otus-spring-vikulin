package ru.otus.hw.repositories;

import base.ConfigurableByPropertiesTestBase;
import data.TestDataProvider;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.contracts.CommentRepository;
import ru.otus.hw.utils.factories.exceptions.LoggedExceptionFactoryImpl;
import utils.BookCheckerImpl;
import utils.contracts.BookChecker;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("Negative tests pack for <Comment> repository")
@Import({JpaCommentRepository.class, CommentConverter.class, BookCheckerImpl.class, TestDataProvider.class,
         LoggedExceptionFactoryImpl.class})
class CommentRepositoryNegativeTest extends ConfigurableByPropertiesTestBase {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    BookChecker bookChecker;

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    TestDataProvider testDataProvider;

    //-----SELECT------
    @Test
    @DisplayName("Throws on finding the comments of book, which not exists")
    void findAllByBookId() {
        assertTrue(commentRepository.findAllByBookId(555L).isEmpty());
    }

    @Test
    @DisplayName("Throws on trying to find the comments of the book by impossible book's ID value")
    void throwsOnFindingByImpossibleId() {
        assertThrows(EntityValidationException.class, ()-> commentRepository.findAllByBookId(0L));
    }

    @Test
    @DisplayName("Throws on trying to find the comments by their ID tags, which are not exists")
    void throwsOnFindingAllByImpossibleIds() {
        assertThrows(EntityValidationException.class, ()-> commentRepository.findAllByIds(Set.of(0L, 1L, 2L)));
    }

    //-----UPDATE------
    @Test
    @DisplayName("Throws on updating the comment's text to null")
    void throwsOnUpdatingTitleWithNull() {
        var comment = testDataProvider.getTestComments().get(1);
        assertThrows(NullPointerException.class, ()-> comment.setText(null));
    }

    @Test
    @DisplayName("Throws on updating the comment's text to empty string")
    void throwsOnUpdatingTitleWithEmptyString() {
        var comment = testDataProvider.getTestComments().get(1);
        comment.setText("");
        assertThrows(ConstraintViolationException.class,
                ()-> {
                    commentRepository.save(comment);
                    this.testEntityManager.flush();
                }
        );
    }

    @Test
    @DisplayName("Throws on updating the comment's book ID by impossible value")
    void throwsOnUpdatingCommentsBookIdByImpossibleValue() {
        var comment = testDataProvider.getTestComments().get(1);
        comment.setBookId(0L);
        assertThrows(EntityValidationException.class, ()-> commentRepository.save(comment));
    }

    @Test
    @DisplayName("Throws on updating the comment's book ID by the value of book, which not exists")
    void throwsOnUpdatingCommentsBookIdByInexistingValue() {
        var comment = testDataProvider.getTestComments().get(1);
        comment.setBookId(555L);
        assertThrows(HibernateException.class,
                     ()-> {
                        commentRepository.save(comment);
                        this.testEntityManager.flush();
                     });
    }

    //-----INSERT------
    @Test
    @DisplayName("Throws on inserting the comment without the title (empty string)")
    void throwsOnInsertingWithTitleWithEmptyString() {
        var newComment = new Comment(1L,"");
        assertThrows(AppInfrastructureException.class, ()-> commentRepository.save(newComment));
    }

    @Test
    @DisplayName("Throws on inserting the comment with book ID value, which not exists")
    void throwsOnInsertingCommentsBookIdByInexistingValue() {
        var newComment = new Comment(555L,"");
        assertThrows(AppInfrastructureException.class,
                    ()-> {
                        commentRepository.save(newComment);
                        this.testEntityManager.flush();
                    });
    }


    //-----DELETE------
    @DisplayName("Throws on deleting the comment, whith impossible ID value")
    @Test
    void throwsOnDeleteByImpossibleId() {
        assertThrows(EntityValidationException.class, ()-> commentRepository.deleteById(0L));
    }

    @DisplayName("Throws on deleting the book, which ID is not exists")
    @Test
    void throwsOnDeleteByIdWhichNotExists() {
        assertThrows(EntityNotFoundException.class, ()-> commentRepository.deleteById(666L));
    }
}