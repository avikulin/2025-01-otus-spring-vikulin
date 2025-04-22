package data;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CommentsArgProvider implements ArgumentsProvider {
    private final List<Book> books;
    public CommentsArgProvider() {
        var provider = new TestDataProvider();
        this.books = provider.getTestBooks();
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext){
        var names = List.of("Book with no comments",
                            "Book with 1 comment",
                            "Book with 2 comments");
        return IntStream.range(0, this.books.size()).mapToObj(i -> Arguments.of(names.get(i), books.get(i)));
    }
}
