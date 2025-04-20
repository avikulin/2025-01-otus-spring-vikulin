package ru.otus.hw.repositories.data;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BooksArgProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext){
        var names = List.of("Book with 1 author and 1 genre",
                            "Book with 2 authors and 1 genre",
                            "Book with 2 authors and 2 genres");
        var books = TestDataProvider.getTestBooks();
        return IntStream.range(0,books.size()).mapToObj(i -> Arguments.of(names.get(i), books.get(i)));
    }
}
