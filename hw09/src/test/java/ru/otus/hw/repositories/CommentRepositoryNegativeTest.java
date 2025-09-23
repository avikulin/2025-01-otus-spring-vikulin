package ru.otus.hw.repositories;

import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.data.TestDataProvider;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.utils.factories.exceptions.LoggedExceptionFactoryImpl;
import ru.otus.hw.utils.BookCheckerImpl;
import ru.otus.hw.utils.contracts.BookChecker;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("Negative tests pack for <Comment> repository")
@Import({CommentConverter.class, BookCheckerImpl.class, TestDataProvider.class, LoggedExceptionFactoryImpl.class})
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
        assertTrue(commentRepository.findAllByBookIdWithCheck(555L).isEmpty());
    }

    @Test
    @DisplayName("Throws on trying to find the comments of the book by impossible book's ID value")
    void throwsOnFindingByImpossibleId() {
        assertThrows(InvalidDataAccessApiUsageException.class, ()-> commentRepository.findAllByBookIdWithCheck(0L));
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
        assertThrows(ConstraintViolationException.class,
                ()-> {
                    commentRepository.save(comment);
                    this.testEntityManager.flush();
                });
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
        assertThrows(DataIntegrityViolationException.class, ()-> commentRepository.save(newComment));
    }

    @Test
    @DisplayName("Throws on inserting the comment with book ID value, which not exists")
    void throwsOnInsertingCommentsBookIdByInexistingValue() {
        var newComment = new Comment(555L,"");
        assertThrows(DataIntegrityViolationException.class,
                    ()-> {
                        commentRepository.save(newComment);
                        this.testEntityManager.flush();
                    });
    }


    //-----DELETE------
    @DisplayName("Throws on deleting the comment, whith impossible ID value")
    @Test
    void throwsOnDeleteByImpossibleId() {
        assertThrows(InvalidDataAccessApiUsageException.class, ()-> commentRepository.deleteById(0L));
    }

    @DisplayName("Throws on deleting the book, which ID is not exists")
    @Test
    void throwsOnDeleteByIdWhichNotExists() {
        assertThrows(EntityNotFoundException.class, ()-> commentRepository.deleteById(666L));
    }
}