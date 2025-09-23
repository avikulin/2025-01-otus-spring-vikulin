package ru.otus.hw.factories.metadata;

import lombok.Getter;

public enum LinkKey {
    NAV_ROOT_PAGE("Home", "index", "/"),
    NAV_AUTHORS_LIST("Authors", "author/authors", "/authors"),
    NAV_AUTHOR_DETAILS(null, "author/author_details", "/authors/{0}"),
    NAV_GENRES_LIST("Genres", "genre/genres", "/genres"),
    NAV_GENRE_DETAILS(null, "genre/genre_details", "/genres/{0}"),
    NAV_BOOKS_LIST("Books", "book/books", "/books"),
    NAV_BOOK_DETAILS(null, "book/book_details", "/books/{0}"),
    NAV_BOOK_COMMENT_DETAILS(null, "comment/comment_edit", "/books/{0}/comments/{1}");

    @Getter
    private final String title;
    private final String viewPath;

    @Getter
    private final String url;

    private static final String NAV_TEMPLATES_ROOT = "public";

    LinkKey(String title, String viewName, String url) {
        this.title = title;
        this.viewPath = viewName;
        this.url = url;
    }

    public String getViewPath() {
        return NAV_TEMPLATES_ROOT + "/" + viewPath;
    }

}
