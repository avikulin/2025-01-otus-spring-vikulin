package ru.otus.hw.factories;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.entities.AuthorDto;
import ru.otus.hw.dto.entities.BookDto;
import ru.otus.hw.dto.entities.CommentDto;
import ru.otus.hw.dto.entities.GenreDto;
import ru.otus.hw.dto.navigation.NavigationItemDto;
import ru.otus.hw.dto.navigation.NavigationStateDto;
import ru.otus.hw.factories.contracts.LinkFactory;
import ru.otus.hw.factories.contracts.NavigationFactory;
import ru.otus.hw.factories.metadata.LinkKey;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NavigationFactoryImpl implements NavigationFactory {

    LinkFactory factory;

    private List<NavigationItemDto> constructLeftSideBar(){
        return List.of(
                factory.getUndirectedLink(LinkKey.NAV_BOOKS_LIST),
                factory.getUndirectedLink(LinkKey.NAV_GENRES_LIST),
                factory.getUndirectedLink(LinkKey.NAV_AUTHORS_LIST)
        );
    }

    @Override
    public NavigationStateDto getHomeRootNav() {
        return new NavigationStateDto(
                List.of(factory.getUndirectedLink(LinkKey.NAV_ROOT_PAGE)),
                constructLeftSideBar()
        );
    }

    @Override
    public NavigationStateDto getBooksListNav() {
        return new NavigationStateDto(
                List.of(
                        factory.getUndirectedLink(LinkKey.NAV_ROOT_PAGE),
                        factory.getUndirectedLink(LinkKey.NAV_BOOKS_LIST)
                ),
                constructLeftSideBar()
        );
    }

    @Override
    public NavigationStateDto getBookDetailsNav(BookDto book) {
        return new NavigationStateDto(
                List.of(
                        factory.getUndirectedLink(LinkKey.NAV_ROOT_PAGE),
                        factory.getUndirectedLink(LinkKey.NAV_BOOKS_LIST),
                        factory.getDirectedLink(LinkKey.NAV_BOOK_DETAILS, book.getTitle(), book.getId())
                ),
                constructLeftSideBar()
        );
    }

    @Override
    public NavigationStateDto getGetAuthorsListNav() {
        return new NavigationStateDto(
                List.of(
                        factory.getUndirectedLink(LinkKey.NAV_ROOT_PAGE),
                        factory.getUndirectedLink(LinkKey.NAV_AUTHORS_LIST)
                ),
                constructLeftSideBar()
        );
    }

    @Override
    public NavigationStateDto getAuthorDetailsNav(AuthorDto author) {
        return new NavigationStateDto(
                List.of(
                        factory.getUndirectedLink(LinkKey.NAV_ROOT_PAGE),
                        factory.getUndirectedLink(LinkKey.NAV_AUTHORS_LIST),
                        factory.getDirectedLink(LinkKey.NAV_AUTHOR_DETAILS, author.getFullName(), author.getId())
                ),
                constructLeftSideBar()
        );
    }

    @Override
    public NavigationStateDto getGenresListNav() {
        return new NavigationStateDto(
                List.of(
                        factory.getUndirectedLink(LinkKey.NAV_ROOT_PAGE),
                        factory.getUndirectedLink(LinkKey.NAV_GENRES_LIST)
                ),
                constructLeftSideBar()
        );
    }

    @Override
    public NavigationStateDto getGenreDetailsNav(GenreDto genre) {
        var link = factory.getUndirectedLink(LinkKey.NAV_GENRE_DETAILS);
        return new NavigationStateDto(
                List.of(
                        factory.getUndirectedLink(LinkKey.NAV_ROOT_PAGE),
                        factory.getUndirectedLink(LinkKey.NAV_AUTHORS_LIST),
                        factory.getDirectedLink(LinkKey.NAV_GENRE_DETAILS, link.getTitle(), genre.getId())
                ),
                constructLeftSideBar()
        );
    }
}
