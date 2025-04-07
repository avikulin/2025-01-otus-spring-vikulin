create schema if not exists otus_hw_05;

drop table if exists otus_hw_05.authors;
create table otus_hw_05.authors (
    id bigserial,
    full_name varchar(255),
    primary key (id)
);

drop table if exists otus_hw_05.genres;
create table otus_hw_05.genres (
    id bigserial,
    name varchar(255),
    primary key (id)
);

drop table if exists otus_hw_05.books;
create table otus_hw_05.books (
    id bigserial,
    title varchar(255),
    author_id bigint references otus_hw_05.authors (id) on delete cascade,
    primary key (id)
);

drop table if exists otus_hw_05.books_genres;
create table otus_hw_05.books_genres (
    book_id bigint references otus_hw_05.books(id) on delete cascade,
    genre_id bigint references otus_hw_05.genres(id) on delete cascade,
    primary key (book_id, genre_id)
);