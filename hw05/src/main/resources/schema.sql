DROP SCHEMA IF EXISTS OTUS_HW_05 CASCADE;
CREATE SCHEMA OTUS_HW_05;

CREATE SEQUENCE OTUS_HW_05.AUTHOR_ID_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE OTUS_HW_05.GENRE_ID_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE OTUS_HW_05.BOOK_ID_SEQ START WITH 1 INCREMENT BY 1;


CREATE TABLE OTUS_HW_05.AUTHORS (
    ID BIGINT DEFAULT NEXT VALUE FOR OTUS_HW_05.AUTHOR_ID_SEQ,
    FULL_NAME VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE OTUS_HW_05.GENRES (
    ID BIGINT DEFAULT NEXT VALUE FOR OTUS_HW_05.GENRE_ID_SEQ,
    NAME VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE OTUS_HW_05.BOOKS (
    ID BIGINT DEFAULT NEXT VALUE FOR OTUS_HW_05.BOOK_ID_SEQ,
    TITLE VARCHAR(255) NOT NULL,
    YEAR_OF_PUBLISHED INTEGER NOT NULL ,
    PRIMARY KEY (ID)
);

CREATE TABLE OTUS_HW_05.LNK_BOOKS_AUTHORS (
     BOOK_ID BIGINT NOT NULL REFERENCES OTUS_HW_05.BOOKS(ID) ON DELETE RESTRICT,
     AUTHOR_ID BIGINT  NOT NULL REFERENCES OTUS_HW_05.AUTHORS(ID) ON DELETE RESTRICT,
     PRIMARY KEY (BOOK_ID, AUTHOR_ID)
);

CREATE TABLE OTUS_HW_05.LNK_BOOKS_GENRES (
    BOOK_ID BIGINT  NOT NULL REFERENCES OTUS_HW_05.BOOKS(ID) ON DELETE RESTRICT,
    GENRE_ID BIGINT  NOT NULL REFERENCES OTUS_HW_05.GENRES(ID) ON DELETE RESTRICT,
    PRIMARY KEY (BOOK_ID, GENRE_ID)
);