package ru.otus.hw.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.factories.contracts.NavigationFactory;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.repositories.BookRepository;

import static ru.otus.hw.controllers.Constants.MODEL_KEY_NAVIGATION_STATE;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {
    static String MODEL_KEY_CONTENT = "data";

    BookRepository bookRepository;

    NavigationFactory navFactory;

    BookMapper bookMapper;

    @GetMapping("/books")
    public String listAllBooks(Model model){
        var books = bookRepository.findAllBatched();
        var navState = navFactory.getBooksListNav();
        model.addAttribute(MODEL_KEY_CONTENT, bookMapper.toDtoList(books));
        model.addAttribute(MODEL_KEY_NAVIGATION_STATE, navState);
        return navState.getTemplatePath();
    }

    @GetMapping("/books/{bookId}")
    public String getBookDetails(@PathVariable("bookId") long bookId, Model model) {
        var book = bookRepository.findByIdWithCheck(bookId).orElseThrow();
        var bookDto = bookMapper.toDto(book);
        var navState = navFactory.getBookDetailsNav(bookDto);
        model.addAttribute(MODEL_KEY_CONTENT, bookDto);
        model.addAttribute(MODEL_KEY_NAVIGATION_STATE, navState);
        return navState.getTemplatePath();
    }
}
