package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.entities.AuthorDto;
import ru.otus.hw.mappers.config.BaseConfig;
import ru.otus.hw.mappers.base.BaseMapper;
import ru.otus.hw.models.Author;

import java.util.Collection;
import java.util.List;

/*
    Маппинг плоски и прямой. И вроде бы можно не объявлять примитивный маппер...
    Но все же, имеет смысл его определить для отлова расхождений в полях сущности и DTO,
    а также для авторегистрации реализации маппера в Spring Context.
 */
@Mapper(config = BaseConfig.class)
public interface AuthorMapper extends BaseMapper<Author, AuthorDto> {
    @Override
    AuthorDto toDto(Author entity);

    @Override
    Author toEntity(AuthorDto dto);

    @Override
    List<AuthorDto> toDtoList(Collection<Author> entities);

    @Override
    List<Author> toEntitiesList(Collection<AuthorDto> dtos);
}
