package ru.otus.hw.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.otus.hw.models.contracts.CatalogEntity;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book implements CatalogEntity {

    @Setter
    long id;

    String title;

    int yearOfPublished;

    List<Author> authors;

    List<Genre> genres;
}
