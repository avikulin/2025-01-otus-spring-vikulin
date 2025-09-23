package ru.otus.hw.dto.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookDto {
    long id;
    int yearOfPublished;
    String title;
    List<AuthorDto> authors;
    List<GenreDto> genres;
    List<CommentDto> comments;
}
