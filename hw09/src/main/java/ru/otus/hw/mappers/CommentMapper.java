package ru.otus.hw.mappers;


import org.mapstruct.Mapper;
import ru.otus.hw.dto.entities.CommentDto;
import ru.otus.hw.mappers.config.BaseConfig;
import ru.otus.hw.mappers.base.BaseMapper;
import ru.otus.hw.models.Comment;

import java.util.Collection;
import java.util.List;

/*
    Маппинг плоски и прямой. И вроде бы можно не объявлять примитивный маппер...
    Но все же, имеет смысл его определить для отлова расхождений в полях сущности и DTO,
    а также для авторегистрации реализации маппера в Spring Context.
 */
@Mapper(config = BaseConfig.class)
public interface CommentMapper extends BaseMapper<Comment, CommentDto> {
    @Override
    CommentDto toDto(Comment entity);

    @Override
    Comment toEntity(CommentDto dto);

    @Override
    List<CommentDto> toDtoList(Collection<Comment> entities);

    @Override
    List<Comment> toEntitiesList(Collection<CommentDto> dtos);
}
