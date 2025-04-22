package utils;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import utils.contracts.DeepCloneUtils;

@Component
public class DeepCloneUtilsImpl implements DeepCloneUtils {
    @Override
    public Book cloneBook(Book book){
        var bookId = book.getId();
        return generalCloneBook(book, bookId);
    }

    @Override
    public Book cloneBookAsNew(Book book){
        return generalCloneBook(book, 0);
    }

    private static Book generalCloneBook(Book book, long bookId){
        var bookClone = new Book(bookId, book.getTitle(), book.getYearOfPublished());
        var destAuthorsCollection = bookClone.getAuthors();
        book.getAuthors().forEach(a->{
            var author = new Author(a.getId(), a.getFullName());
            destAuthorsCollection.add(author);
        });
        var destGenresCollection = bookClone.getGenres();
        book.getGenres().forEach(g->{
            var genre = new Genre(g.getId(), g.getName());
            destGenresCollection.add(genre);
        });
        var destCommentsCollection = bookClone.getComments();
        book.getComments().forEach(c->{
            var comment = new Comment(c.getBookId(), c.getText());
            destCommentsCollection.add(comment);
        });
        return bookClone;
    }
}
