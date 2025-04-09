package ru.otus.hw.repositories;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.config.AppConfig;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.MoreThanOneEntityFound;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.contracts.AuthorRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JdbcAuthorRepository implements AuthorRepository {
    AppConfig appConfig;
    NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Author> findAll() {
        Objects.requireNonNull(appConfig, "Application config can't be null");
        try {
            return jdbc.query(
                    "SELECT ID, FULL_NAME FROM %s.AUTHORS".formatted(appConfig.getSchemaName()),
                    new AuthorRowMapper()
            );
        } catch (DataAccessException e){
            var msg = "Something went wrong with access to DB while finding authors";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    @Override
    public Optional<Author> findById(long id) {
        Objects.requireNonNull(appConfig, "Application config can't be null");
        var filterParams = Map.of("id", id);
        Author dataItem;
        try{
            dataItem = jdbc.queryForObject(
                            "SELECT ID, FULL_NAME FROM %s.AUTHORS WHERE ID = :id"
                                    .formatted(appConfig.getSchemaName()),
                            filterParams,
                            new AuthorRowMapper()
            );
            if (dataItem == null) {
                throw new EntityNotFoundException("Author not found for ID: " + id);
            }
            return Optional.of(dataItem);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Author not found for ID: " + id);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("Database inconsistency: Multiple authors found for primary key: {}", id);
            throw new MoreThanOneEntityFound("Multiple authors found for ID: " + id);
        } catch (DataAccessException e){
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
