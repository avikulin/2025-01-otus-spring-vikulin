package ru.otus.hw.factories.contracts;

import ru.otus.hw.dto.entities.AuthorDto;
import ru.otus.hw.dto.entities.BookDto;
import ru.otus.hw.dto.entities.CommentDto;
import ru.otus.hw.dto.entities.GenreDto;
import ru.otus.hw.dto.navigation.NavigationStateDto;

public interface NavigationFactory {
    NavigationStateDto getHomeRootNav();

    NavigationStateDto getBooksListNav();

    NavigationStateDto getBookDetailsNav(BookDto book);

    NavigationStateDto getGetAuthorsListNav();

    NavigationStateDto getAuthorDetailsNav(AuthorDto author);

    NavigationStateDto getGenresListNav();

    NavigationStateDto getGenreDetailsNav(GenreDto genre);
}
