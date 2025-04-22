package data;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestDataProvider {
    final Map<Integer, Author> authors = Map.of(
            1, new Author(1, "Author-1"),
            2, new Author(2, "Author-2"),
            3, new Author(3, "Author-3")
    );
    final Map<Integer, Genre> genres = Map.of(
            1, new Genre(1, "Genre-1"),
            2, new Genre(2,"Genre-2"),
            3, new Genre(3,"Genre-3")
    );
    final Map<Integer, Comment> comments = Map.of(
            1, new Comment(1, 2, "COMMENT-2-1"),
            2, new Comment(2, 3, "COMMENT-3-1"),
            3, new Comment(3, 3, "COMMENT-3-2")
    );
    final Map<Integer, Book> books = new HashMap<>();

    public TestDataProvider() {
        var first = new Book(1L, "Book-1", 2001);
        first.getAuthors().add(authors.get(1));
        first.getGenres().add(genres.get(1));
        this.books.put(1, first);

        var second = new Book(2L, "Book-2", 2002);
        second.getAuthors().addAll(
                Set.of(
                        authors.get(1),
                        authors.get(2)
                )
        );
        second.getGenres().add(genres.get(2));
        second.getComments().add(comments.get(1));
        this.books.put(2, second);

        var third = new Book(3L, "Book-3", 2003);
        third.getAuthors().addAll(
                Set.of(
                        authors.get(1),
                        authors.get(2)
                )
        );
        third.getGenres().addAll(
                Set.of(
                        genres.get(1),
                        genres.get(2)
                )
        );
        third.getComments().addAll(
                Set.of(
                        comments.get(2),
                        comments.get(3)
                )
        );
        this.books.put(3, third);
    }

    public Author getTestAuthorById(int Id){
        return authors.get(Id);
    }

    public List<Author> getTestAuthors() {return new ArrayList<>(authors.values());}

    public Genre getTestGenreById(int Id){
        return genres.get(Id);
    }

    public List<Genre> getTestGenres() {return new ArrayList<>(genres.values());}

    public List<Comment> getTestComments() {return new ArrayList<>(comments.values());}

    public List<Book> getTestBooks(){
        return new ArrayList<>(books.values());
    }
}