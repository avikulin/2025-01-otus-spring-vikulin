package ru.otus.hw.repositories;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.config.AppConfig;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.exceptions.MoreThanOneEntityFound;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.AppInfrastructureException;
import ru.otus.hw.exceptions.SqlCommandFailure;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.relations.BookAuthorRelation;
import ru.otus.hw.models.relations.BookGenreRelation;
import ru.otus.hw.repositories.contracts.AuthorRepository;
import ru.otus.hw.repositories.contracts.BookRepository;
import ru.otus.hw.repositories.contracts.GenreRepository;
import ru.otus.hw.repositories.mappers.BookResultSetExtractor;
import ru.otus.hw.repositories.mappers.BookRowMapper;
import ru.otus.hw.repositories.mappers.LnkBooksAuthorsRowMapper;
import ru.otus.hw.repositories.mappers.LnkBooksGenresRowMapper;
import ru.otus.hw.utils.sql.contracts.CommandNormalizer;
import ru.otus.hw.utils.validators.BookValidator;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Slf4j
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JdbcBookRepository implements BookRepository {

    AppConfig appConfig;

    CommandNormalizer normalizer;

    AuthorRepository authorRepository;

    GenreRepository genreRepository;

    BookConverter bookConverter;

    AuthorConverter authorConverter;

    GenreConverter genreConverter;

    BookValidator bookValidator;

    NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<Book> findById(long id) {
        if (id < 1) {
            log.error("Trying to process impossible value as ID for find operation");
            throw new EntityValidationException("The ID must be 1 or greater");
        }
        var filterParams = Map.of("id", id);
        List<Book> dataItems;
        try {
            dataItems = jdbc.query(
                this.normalizer.normalize(
                    """
                            SELECT
                                B.ID AS BOOK_ID,
                                B.TITLE AS BOOK_TITLE,
                                B.YEAR_OF_PUBLISHED AS BOOK_YEAR_OF_PUBLISHED,
                                A.ID AS AUTHOR_ID,
                                A.FULL_NAME AS AUTHOR_FULL_NAME,
                                G.ID AS GENRE_ID,
                                G.NAME AS GENRE_NAME\s
                            FROM ${schema_name}.BOOKS B
                                LEFT JOIN ${schema_name}.LNK_BOOKS_AUTHORS LBA
                                    ON b.ID = LBA.BOOK_ID
                                        LEFT JOIN ${schema_name}.AUTHORS A
                                            ON LBA.AUTHOR_ID = A.ID
                                LEFT JOIN ${schema_name}.LNK_BOOKS_GENRES LBG
                                    ON B.ID = LBG.BOOK_ID
                                        LEFT JOIN ${schema_name}.GENRES G
                                            ON G.ID = LBG.GENRE_ID\s
                            WHERE B.ID = :id
                                ORDER BY B.ID, A.ID, G.ID"""
                ),
                filterParams,
                new BookResultSetExtractor()
            );
            if (dataItems == null || dataItems.isEmpty()) {
                throw new EntityNotFoundException("Book not found for ID: " + id);
            }
            if (dataItems.size() > 1) {
                var items = dataItems.stream()
                        .map(Book::toString)
                        .collect(Collectors.joining(System.lineSeparator()));
                var msg = "Found more than one book for ID: %s\n{%s}".formatted(id, items);
                log.error(msg);
                throw new MoreThanOneEntityFound("Found more than one book for ID: " + id);
            }
            var res = dataItems.get(0);
            return Optional.of(res);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Author not found for ID: " + id);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("Database inconsistency: Multiple authors found for primary key: {}", id);
            throw new MoreThanOneEntityFound("Multiple authors found for ID: " + id);
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while finding genres";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    @Override
    public List<Book> findAll() {
        var authors = authorRepository.findAll();
        var genres = genreRepository.findAll();
        var bookToAuthorRelations = getAllAuthorRelations();
        var bookToGenreRelations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        this.mergeBooksInfo(books, authors, genres, bookToAuthorRelations, bookToGenreRelations);
        return books;
    }

    @Override
    public Book save(Book book) {
        this.bookValidator.validate(book);
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        if (id < 1) {
            log.error("Trying to process impossible value as ID for delete operation");
            throw new EntityValidationException("The ID must be 1 or greater");
        }
        var params = Map.of("book_id", String.valueOf(id));
        try {
            // зачищаем зависимые сущности. Если такой книги нет - то запросы не нарушат согласованности данных.
            this.jdbc.update(
                this.normalizer.normalize(
                    """
                             DELETE FROM ${schema_name}.LNK_BOOKS_AUTHORS WHERE BOOK_ID = :book_id;
                             DELETE FROM ${schema_name}.LNK_BOOKS_GENRES  WHERE BOOK_ID = :book_id"""
                ),
                params
            );
            // зачищаем корень агрегата
            var rowsAffected = this.jdbc.update(
                this.normalizer.normalize(
                    """
                            DELETE FROM ${schema_name}.BOOKS\s
                            WHERE ID = :book_id"""
                    ),params
            );
            if (rowsAffected == 0) {
                throw new EntityNotFoundException("Book not found for ID: " + id);
            }
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while deleting book with ID " + id;
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    private List<Book> getAllBooksWithoutGenres() {
        Objects.requireNonNull(appConfig, "Application config can't be null");
        var params = Map.of("threshold", this.appConfig.getInMemoryLoadThreshold());
        try {
            return jdbc.query(
                this.normalizer.normalize(
                    """
                            SELECT
                                   ID,
                                   TITLE,
                                   YEAR_OF_PUBLISHED\s
                            FROM
                                   ${schema_name}.BOOKS\s
                            LIMIT :threshold"""
                ),
                params,
                new BookRowMapper()
            );
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while getting books";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        Objects.requireNonNull(appConfig, "Application config can't be null");
        var params = Map.of("threshold", this.appConfig.getInMemoryLoadThreshold());
        try {
            return jdbc.query(
                this.normalizer.normalize(
                    """
                            SELECT
                                 BOOK_ID,
                                 GENRE_ID\s
                            FROM
                                 ${schema_name}.LNK_BOOKS_GENRES\s
                            LIMIT :threshold"""
                ),
                params,
                new LnkBooksGenresRowMapper()
            );
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while getting books-to-genres relations";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    private List<BookAuthorRelation> getAllAuthorRelations() {
        Objects.requireNonNull(appConfig, "Application config can't be null");
        var params = Map.of("threshold", this.appConfig.getInMemoryLoadThreshold());
        try {
            return jdbc.query(
                this.normalizer.normalize(
                    """
                            SELECT
                               BOOK_ID,
                               AUTHOR_ID\s
                            FROM
                               ${schema_name}.LNK_BOOKS_AUTHORS\s
                            LIMIT :threshold"""
                ),
                params,
                new LnkBooksAuthorsRowMapper()
            );
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while getting books-to-authors relations";
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres,
                                List<Author> authors,
                                List<Genre> genres,
                                List<BookAuthorRelation> bookAuthorRelations,
                                List<BookGenreRelation> bookGenreRelations) {
        var genresDict = genres.stream().collect(Collectors.toMap(Genre::getId, genre -> genre));
        var authorsDict = authors.stream().collect(Collectors.toMap(Author::getId, author -> author));
        var authorRelationsDict = bookAuthorRelations.stream().collect(
                                                                    Collectors.groupingBy(
                                                                            BookAuthorRelation::bookId,
                                                                            mapping(
                                                                                    BookAuthorRelation::authorId,
                                                                                    toList()
                                                                            )
                                                                    ));
        var genresRelationDict = bookGenreRelations.stream().collect(
                                                                    Collectors.groupingBy(
                                                                            BookGenreRelation::bookId,
                                                                            mapping(BookGenreRelation::genreId,
                                                                                    toList()
                                                                            )
                                                                    ));
        for (var book : booksWithoutGenres) {
            var authorsList = authorRelationsDict.get(book.getId()).stream().map(authorsDict::get).toList();
            var genresList = genresRelationDict.get(book.getId()).stream().map(genresDict::get).toList();
            book.getAuthors().addAll(authorsList);
            book.getGenres().addAll(genresList);
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        var params = new BeanPropertySqlParameterSource(book);
        // вставляем запись корня агрегата
        try {
            var res = this.jdbc.update(
                this.normalizer.normalize(
                    """
                            INSERT INTO
                                ${schema_name}.BOOKS (TITLE, YEAR_OF_PUBLISHED)\s
                            VALUES
                                (:title, :yearOfPublished)"""
                ),
                params,
                keyHolder,
                new String[] {"ID"}
            );
            if (res == 0) {
                throw new SqlCommandFailure("Insert failed due to DB error");
            }
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB " +
                    "while inserting the book: " + this.bookConverter.bookToString(book);
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
        var newId = keyHolder.getKeyAs(Long.class);
        book.setId(Objects.requireNonNull(newId));

        // добавляем записи зависимых сущностей агрегата
        removeAuthorsRelationsFor(book);
        batchInsertAuthorsRelationsFor(book);
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }


    private Book update(Book book) {
        var params = new BeanPropertySqlParameterSource(book);
        try {
            var res = this.jdbc.update(
                this.normalizer.normalize(
                    """
                            UPDATE
                                ${schema_name}.BOOKS\s
                            SET
                                TITLE = :title,
                                YEAR_OF_PUBLISHED = :yearOfPublished\s
                            WHERE
                                ID = :id"""),
                params
            );
            if (res == 0) {
                throw new EntityNotFoundException("Update failed due to book with ID %s was not found"
                                                  .formatted(book.getId()));
            }
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB " +
                      "while updating the book with ID " + book.getId();
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }

        removeAuthorsRelationsFor(book);
        batchInsertAuthorsRelationsFor(book);
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertAuthorsRelationsFor(Book book) {

        var authorsBatch = book.getAuthors().stream()
                                .map(a -> new BookAuthorRelation(book.getId(), a.getId()))
                                .map(BeanPropertySqlParameterSource::new)
                                .toArray(SqlParameterSource[]::new);
        int[] results;
        try {
            results = this.jdbc.batchUpdate(
                this.normalizer.normalize(
                    """
                            INSERT INTO
                               ${schema_name}.LNK_BOOKS_AUTHORS (BOOK_ID, AUTHOR_ID)\s
                            VALUES
                                   (:bookId, :authorId)"""),
                    authorsBatch
            );
            var failures = Arrays.stream(results)
                    .filter(x -> x == 0)
                    .mapToObj(i -> book.getAuthors().get(i))
                    .toList();
            if (!failures.isEmpty()) {
                var authorsErrors = failures.stream()
                        .map(this.authorConverter::authorToString)
                        .collect(Collectors.joining(", "));
                var msg = ("The following authors for book with ID = %d " +
                        "were not inserted due to DB error:%s").formatted(book.getId(), authorsErrors);
                throw new SqlCommandFailure(msg);
            }
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while batch " +
                      "inserting genres for book with ID " + book.getId();
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }


    private void batchInsertGenresRelationsFor(Book book) {
        var genresBatch = book.getGenres().stream()
                .map(g -> new BookGenreRelation(book.getId(), g.getId()))
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
        int[] results;
        try {
            results = this.jdbc.batchUpdate(
                this.normalizer.normalize(
                    """
                            INSERT INTO
                               ${schema_name}.LNK_BOOKS_GENRES (BOOK_ID, GENRE_ID)\s
                            VALUES\s
                               (:bookId, :genreId)"""),
                genresBatch
            );
            var failures = Arrays.stream(results)
                                 .filter(x -> x == 0)
                                 .mapToObj(i -> book.getGenres().get(i))
                                 .toList();
            if (!failures.isEmpty()) {
                var genresErrors = failures.stream()
                                           .map(this.genreConverter::genreToString)
                                           .collect(Collectors.joining(", "));
                var msg = ("The following genres for book with ID = %d " +
                           "were not inserted due to DB error:%s").formatted(book.getId(), genresErrors);
                throw new SqlCommandFailure(msg);
            }
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while batch " +
                      "inserting genres for book with ID " + book.getId();
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    private void removeGenresRelationsFor(Book book) {
        Objects.requireNonNull(appConfig, "Application config can't be null");
        Objects.requireNonNull(book, "Book can't be null");
        var params = Map.of("book_id", String.valueOf(book.getId()));
        try {
            this.jdbc.update(
                this.normalizer.normalize(
                    """
                             DELETE FROM ${schema_name}.LNK_BOOKS_GENRES\s
                             WHERE BOOK_ID = :book_id""")
                ,params
            );
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while clearing genres for book with ID " + book.getId();
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }

    private void removeAuthorsRelationsFor(Book book) {
        Objects.requireNonNull(appConfig, "Application config can't be null");
        Objects.requireNonNull(book, "Book can't be null");
        var params = Map.of("book_id", String.valueOf(book.getId()));
        try {
            this.jdbc.update(
                this.normalizer.normalize(
                    """
                             DELETE FROM ${schema_name}.LNK_BOOKS_AUTHORS\s
                             WHERE BOOK_ID = :book_id""")
                ,params
            );
        } catch (DataAccessException e) {
            var msg = "Something went wrong with access to DB while clearing authors for book with ID " + book.getId();
            log.error(msg, e);
            throw new AppInfrastructureException(msg, e);
        }
    }
}
