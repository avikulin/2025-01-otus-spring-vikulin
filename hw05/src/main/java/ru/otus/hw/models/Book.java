package ru.otus.hw.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {
    @Setter
    long id;
    String title;
    int yearOfPublished;
    List<Author> authors;
    List<Genre> genres;
}
