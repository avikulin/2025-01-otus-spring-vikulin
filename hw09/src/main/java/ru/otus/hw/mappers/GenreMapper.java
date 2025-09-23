package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.entities.GenreDto;
import ru.otus.hw.mappers.config.BaseConfig;
import ru.otus.hw.mappers.base.BaseMapper;
import ru.otus.hw.models.Genre;

import java.util.Collection;
import java.util.List;

/*
    Маппинг плоски и прямой. И вроде бы можно не объявлять примитивный маппер...
    Но все же, имеет смысл его определить для отлова расхождений в полях сущности и DTO,
    а также для авторегистрации реализации маппера в Spring Context.
 */
@Mapper(config = BaseConfig.class)
public interface GenreMapper extends BaseMapper<Genre, GenreDto> {
    @Override
    GenreDto toDto(Genre entity);

    @Override
    Genre toEntity(GenreDto dto);

    @Override
    List<GenreDto> toDtoList(Collection<Genre> entities);

    @Override
    List<Genre> toEntitiesList(Collection<GenreDto> dtos);
}
