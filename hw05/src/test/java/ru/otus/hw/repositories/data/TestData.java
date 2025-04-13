package ru.otus.hw.repositories.data;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.params.provider.Arguments;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestData {
    static Map<Integer, Author> authors = Map.of(
            1, new Author(1, "Author-1"),
            2, new Author(2, "Author-2"),
            3, new Author(3, "Author-3")
    );
    static Map<Integer, Genre> genres = Map.of(
            1, new Genre(1, "Genre-1"),
            2, new Genre(2,"Genre-2"),
            3, new Genre(3,"Genre-3")
    );

    public static List<Author> getTestAuthors() {return new ArrayList<>(authors.values());}

    public static List<Genre> getTestGenres() {return new ArrayList<>(genres.values());}

    public static List<Book> getTestBooks(){
        return List.of(
            new Book(1L, "Book-1", 2001,
                    List.of(authors.get(1)),
                    List.of(genres.get(1))
            ),
            new Book(2L, "Book-2", 2002,
                    List.of(authors.get(1), authors.get(2)),
                    List.of(genres.get(2))
            ),
            new Book(3L, "Book-3", 2003,
                    List.of(authors.get(1), authors.get(2)),
                    List.of(genres.get(1), genres.get(2))
            )
        );
    }


}
