package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.entities.BookDto;
import ru.otus.hw.mappers.base.BaseMapper;
import ru.otus.hw.mappers.config.BaseConfig;
import ru.otus.hw.models.Book;

import java.util.Collection;
import java.util.List;

@Mapper(config = BaseConfig.class,
        uses = {AuthorMapper.class, GenreMapper.class, CommentMapper.class})
public interface BookMapper extends BaseMapper<Book, BookDto> {
    @Override
    BookDto toDto(Book entity);

    @Override
    Book toEntity(BookDto dto);

    @Override
    List<BookDto> toDtoList(Collection<Book> entities);

    @Override
    List<Book> toEntitiesList(Collection<BookDto> dtos);
}
