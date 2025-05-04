INSERT INTO OTUS_HW_07_TEST.BOOKS(TITLE, YEAR_OF_PUBLISHED)
VALUES ('Book-1', 2001), --1
       ('Book-2', 2002), --2
       ('Book-3', 2003); --3

INSERT INTO OTUS_HW_07_TEST.AUTHORS(FULL_NAME)
VALUES ('Author-1'), --1
       ('Author-2'), --2
       ('Author-3'); --3


INSERT INTO OTUS_HW_07_TEST.GENRES(NAME)
VALUES ('Genre-1'), --1
       ('Genre-2'), --2
       ('Genre-3'); --3


INSERT INTO OTUS_HW_07_TEST.LNK_BOOKS_AUTHORS(BOOK_ID, AUTHOR_ID)
VALUES (1, 1),
       (2, 1),
       (2, 2),
       (3, 1),
       (3, 2);

INSERT INTO OTUS_HW_07_TEST.LNK_BOOKS_GENRES(BOOK_ID, GENRE_ID)
VALUES (1, 1),
       (2, 2),
       (3, 1),
       (3, 2);

INSERT INTO OTUS_HW_07_TEST.COMMENTS(BOOK_ID, COMMENT_TEXT)
VALUES (2, 'COMMENT-2-1'),
       (3, 'COMMENT-3-1'),
       (3, 'COMMENT-3-2');