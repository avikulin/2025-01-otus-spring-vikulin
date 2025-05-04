package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AuthorRepository extends Repository<Author, Long> {

    List<Author> findAll();

    Optional<Author> findById(long id);

    @Query("SELECT a FROM Author a WHERE a.id IN (:ids)")
    List<Author> findAllByIds(@Param("ids")Set<Long> ids);
}
