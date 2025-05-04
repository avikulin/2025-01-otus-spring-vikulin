package ru.otus.hw.data;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BooksArgProvider implements ArgumentsProvider {
    private final List<Book> books;

    public BooksArgProvider() {
        var provider = new TestDataProvider();
        this.books = provider.getTestBooks();
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext){
        var names = List.of("Book with 1 author and 1 genre",
                            "Book with 2 authors and 1 genre",
                            "Book with 2 authors and 2 genres");
        return IntStream.range(0, this.books.size()).mapToObj(i -> Arguments.of(names.get(i), books.get(i)));
    }
}
