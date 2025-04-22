package ru.otus.hw.commands;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.Validate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.contracts.CommentsService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentCommands {

    CommentsService commentsService;

    CommentConverter commentConverter;

    @ShellMethod(value = "Get comment by it's ID", key = "get-comment")
    String getCommentsById(Long id) {
        var commentItem =  this.commentsService.findById(id);
        Validate.isTrue(commentItem.isPresent(), "Comment with [ID] = %d not found in DB".formatted(id));
        var commentObj = commentItem.get();
        return this.commentConverter.commentToString(commentObj);
    }

    @ShellMethod(value = "List all comments of the book, defined by it's ID", key = "ls-comment")
    String getAllCommentsForBookById(Long bookId) {
        var comments = this.commentsService.findAllByBookId(bookId);
        return comments.stream()
                       .map(commentConverter::commentToString)
                       .collect(Collectors.joining(System.lineSeparator()))
                       + System.lineSeparator();
    }

    @ShellMethod(value = "Put new comment on book, defined by it's ID", key = "add-comment")
    String putCommentToBookById(Long bookId, String commentText) {
        var comment = this.commentsService.insert(bookId, commentText);
        return this.commentConverter.commentToString(comment);
    }


    @ShellMethod(value = "Update an existing comment, defined by it's ID", key = "upd-comment")
    String updateCommentById(Long id, String commentText) {
        var comment = this.commentsService.update(id, commentText);
        return this.commentConverter.commentToString(comment);
    }

    @ShellMethod(value = "Removes comment, defined by it's ID", key = "rm-comment")
    String deleteCommentById(Long id) {
        this.commentsService.deleteById(id);
        return "Done!";
    }
}
