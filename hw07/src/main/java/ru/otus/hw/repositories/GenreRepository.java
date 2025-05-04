package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

public interface GenreRepository extends Repository<Genre, Long> {

    List<Genre> findAll();

    Genre findById(long id);

    @Query("SELECT g FROM Genre g WHERE g.id IN (:ids)")
    List<Genre> findAllByIds(@Param("ids") Set<Long> ids);
}
