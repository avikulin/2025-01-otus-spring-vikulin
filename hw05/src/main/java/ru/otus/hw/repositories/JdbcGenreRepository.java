package ru.otus.hw.repositories;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.contracts.GenreRepository;
import ru.otus.hw.utils.sql.SqlNormalizer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JdbcGenreRepository implements GenreRepository {

    SqlNormalizer sqlNormalizer;

    NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Genre> findAll() {
        try {
            return jdbc.query(
                this.sqlNormalizer.normalize(
                    """
                             SELECT
                                ID,
                                NAME\s
                             FROM
                                ${schema_name}.GENRES"""
                ),
                    new GenreRowMapper()
            );
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while finding genres";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        var filterParams = Map.of("ids_set", ids);
        List<Genre> dataItem;
        try {
            dataItem = jdbc.query(
                this.sqlNormalizer.normalize(
                    """
                             SELECT
                                ID,
                                NAME\s
                             FROM
                                ${schema_name}.GENRES\s
                             WHERE
                                ID IN (:ids_set)"""
                ),
                filterParams,
                new GenreRowMapper()
            );
            return dataItem;
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while finding genres";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            var genreId = rs.getLong("ID");
            var genreName = rs.getString("NAME");
            return new Genre(genreId, genreName);
        }
    }
}
