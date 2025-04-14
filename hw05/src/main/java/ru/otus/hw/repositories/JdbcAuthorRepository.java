package ru.otus.hw.repositories;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.MoreThanOneEntityFound;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.contracts.AuthorRepository;
import ru.otus.hw.utils.sql.SqlNormalizer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JdbcAuthorRepository implements AuthorRepository {

    SqlNormalizer sqlNormalizer;

    NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Author> findAll() {
        try {
            return jdbc.query(
                this.sqlNormalizer.normalize(
                    """
                             SELECT
                                ID,
                                FULL_NAME\s
                             FROM
                                ${schema_name}.AUTHORS"""
                ),
                new AuthorRowMapper()
            );
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while finding authors";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    @Override
    public Optional<Author> findById(long id) {
        var dataItems = this.findAllByIds(Set.of(id));
        if (CollectionUtils.isEmpty(dataItems)) {
            var msg = "Author not found for ID: " + id;
            log.info(msg);
            throw new EntityNotFoundException(msg);
        }
        if (dataItems.size() > 1) {
            log.error("Database inconsistency detected: Multiple authors found for primary key: {}", id);
            throw new MoreThanOneEntityFound("Multiple authors found for ID: " + id);
        }
        return Optional.of(dataItems.get(0));
    }

    @Override
    public List<Author> findAllByIds(Set<Long> ids) {
        var filterParams = Map.of("ids", ids);
        List<Author> dataItems;
        try {
            dataItems = jdbc.query(
                this.sqlNormalizer.normalize(
                        """
                                 SELECT
                                    ID,
                                    FULL_NAME\s
                                 FROM
                                    ${schema_name}.AUTHORS\s
                                 WHERE
                                    ID IN (:ids)"""
                ),
                filterParams,
                new AuthorRowMapper()
            );
            return dataItems;
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while finding genres";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    private static class AuthorRowMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            var authorId = rs.getLong("ID");
            var fullName = rs.getString("FULL_NAME");
            return new Author(authorId, fullName);
        }
    }
}
