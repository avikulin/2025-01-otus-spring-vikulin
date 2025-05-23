package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Genre;

@Component
public class GenreConverter {
    public String genreToString(Genre genre) {
        return "[%d] %s".formatted(genre.getId(), genre.getName());
    }
}
