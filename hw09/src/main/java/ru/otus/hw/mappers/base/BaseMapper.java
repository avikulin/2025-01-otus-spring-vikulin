package ru.otus.hw.mappers.base;

import java.util.Collection;
import java.util.List;

/**
 * Базовый интерфейс двустороннего маппинга.
 * @param <E>    класс сущности
 * @param <D>   класс DTO
 */

public interface BaseMapper<E, D> {
    D toDto(E entity);

    E toEntity(D dto);

    List<D> toDtoList(Collection<E> entities);

    List<E> toEntitiesList(Collection<D> dtos);
}
