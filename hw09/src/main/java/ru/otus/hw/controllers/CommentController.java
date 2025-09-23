package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.dto.entities.CommentDto;
import ru.otus.hw.factories.contracts.LinkFactory;
import ru.otus.hw.factories.metadata.LinkKey;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.services.contracts.CommentsService;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class CommentController {
    CommentsService commentsService;

    LinkFactory linkFactory;

    @GetMapping("/books/{bookId}/comments/{commentId}") // задел под REST, хотя bookId и не нужен
    public String getComment(@PathVariable("bookId") long bookId,
                             @PathVariable("commentId") long commentId, Model model){
        var comment = commentsService.findById(commentId);
        // избыточно в данном случае, но нужно для SOLID
        var targetNav = linkFactory.getDirectedLink(LinkKey.NAV_BOOK_COMMENT_DETAILS, "", bookId, commentId);
        return targetNav.getTemplatePath();
    }

    @PostMapping("/books/{bookId}/comments/{commentId}")
    public String addComment(@PathVariable("bookId") long bookId,
                             @ModelAttribute CommentDto commentDto){
        Objects.requireNonNull(commentDto);
        commentsService.insert(bookId, commentDto.getText());
        var targetNav = linkFactory.getDirectedLink(LinkKey.NAV_BOOK_DETAILS,"", bookId);
        return "redirect:/%s".formatted(targetNav.getUrl());
    }

    @PatchMapping("/books/{bookId}/comments/{commentId}")
    public String updateComment(@PathVariable("bookId") long bookId,
                                @PathVariable("commentId") long commentId,
                                @ModelAttribute CommentDto commentDto){
        Objects.requireNonNull(commentDto);
        commentsService.update(commentId, commentDto.getText());
        var targetNav = linkFactory.getDirectedLink(LinkKey.NAV_BOOK_DETAILS,"", bookId);
        return "redirect:/%s".formatted(targetNav.getUrl());
    }

    @DeleteMapping("/books/{bookId}/comments/{commentId}")
    public String removeComment(@PathVariable("bookId") long bookId,
                                @PathVariable("commentId") long commentId){
        commentsService.deleteById(commentId);
        var targetNav = linkFactory.getDirectedLink(LinkKey.NAV_BOOK_DETAILS,"", bookId);
        return "redirect:/%s".formatted(targetNav.getUrl());
    }
}
