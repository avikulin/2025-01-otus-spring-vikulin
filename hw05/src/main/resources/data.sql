/*
[TEST DATA]
1: Site reliability Engineering, Betsy Beyer, Chris Jones, Jennifer Petoff, Niall Richard Murphy, 2016 #Architecture #Microservices
2: Pro Spring 5 5th edition, Lulliana Cosmina, Rob Harrop, Chris Schaefer, Clarence Ho, 2017 #Java #Spring
3: Java coding problems, Anghel Leonard, 2022  #Java #Algorithms
4: Spring in action 5th edition, Craig Walls, 2024 #Java #Spring
5: Distributed Systems 4th edition, Maarten Van Steen, Andrew S. Tannenbaum, 2025 #Architecture #Mathematics #Algorithms
6: Well grounder Java developer 2nd edition, Benjamin Evans, Jason Clark,  Martijn Verburg, 2022 #Java #Architecture
7: Algorithms in Java 4th edition, by Robert Sedgewick, Wayne Kevin, 2011  #Java #Algorithms #Mathematics
8: Mastering Microservices with Java 3rd edition,  Sourabh Sharma, 2019    #Java #Spring #Microservices
9: Building Microservices, 2nd Edition, Sam Newman, 2021   #Architecture #Microservices

[BOOKS]
1: Site reliability Engineering
2: Pro Spring 5, 5th edition
3: Java coding problems
4: Spring in action, 5th edition
5: Distributed Systems, 4th edition
6: Well grounder Java developer, 2nd edition
7: Algorithms in Java, 4th edition
8: Mastering Microservices with Java, 3rd edition
9: Building Microservices, 2nd Edition

[GENRES]
1: Java
2: Algorithms
3: Mathematics
4: Spring
5: Microservices
6: Architecture

[AUTHORS]
-- 1: Site reliability Engineering
1: Betsy Beyer
2: Chris Jones
3: Jennifer Petoff
4: Niall Richard Murphy

-- 2: Pro Spring 5 5th edition
5: Lulliana Cosmina
6: Rob Harrop
7: Chris Schaefer
8: Clarence Ho

-- 3: Java coding problems
9: Anghel Leonard

-- 4: Spring in action 5th edition
10: Craig Walls

-- 5: Distributed Systems 4th edition
11: Maarten Van Steen
12: Andrew S. Tannenbaum

-- 6: Well grounder Java developer 2nd edition
13: Benjamin Evans
14: Jason Clark
15: Martijn Verburg

-- 7: Algorithms in Java 4th edition
16: Robert Sedgewick
17: Wayne Kevin

-- 8: Mastering Microservices with Java 3rd edition
18: Sourabh Sharma

-- 9: Building Microservices, 2nd Edition
19: Sam Newman

[LNK_BOOKS_AUTHORS]
-- 1: Site reliability Engineering
1, 1
1, 2
1, 3
1, 4

-- 2: Pro Spring 5 5th edition
2, 5
2, 6
2, 7
2, 8

-- 3: Java coding problems
3, 9

-- 4: Spring in action 5th edition
4, 10

-- 5: Distributed Systems 4th edition
5, 11
5, 12

-- 6: Well grounder Java developer 2nd edition
6, 13
6, 14
6, 15

-- 7: Algorithms in Java 4th edition
7, 16
7, 17

-- 8: Mastering Microservices with Java 3rd edition
8, 18

-- 9: Building Microservices, 2nd Edition
9, 19


[LNK_BOOKS_GENRES]

-- 1: #Architecture(6) #Microservices(5)
1, 5
1, 6

-- 2: #Java(1) #Spring(4)
2, 1
2, 4

-- 3: #Java(1) #Algorithms(2)
3, 1
3, 2

-- 4: #Java(1) #Spring(4)
4, 1
4, 4

-- 5: #Architecture(6) #Mathematics(3) #Algorithms(2)
5, 2
5, 3
5, 6

-- 6: #Java(1) #Architecture(6)
6, 1
6, 6

-- 7: #Java(1) #Algorithms(2) #Mathematics(3)
7, 1
7, 2
7, 3

-- 8: #Java(1) #Spring(4) #Microservices(5)
8, 1
8, 4
8, 5

-- 9: #Architecture(6) #Microservices(5)
9, 6
9, 5
*/

INSERT INTO OTUS_HW_05.BOOKS(TITLE, YEAR_OF_PUBLISHED)
VALUES ('Site reliability engineering', 2016), --1
       ('Pro Spring 5, 5th edition', 2017), --2
       ('Java coding problems', 2022), --3
       ('Spring in action, 5th edition', 2024), --4
       ('Distributed Systems, 4th edition', 2025), --5
       ('Well grounder Java developer, 2nd edition', 2022), --6
       ('Algorithms in Java, 4th edition', 2011), --7
       ('Mastering Microservices with Java, 3rd edition', 2019), --8
       ('Building Microservices, 2nd Edition', 2021); --9

INSERT INTO OTUS_HW_05.AUTHORS(FULL_NAME)
VALUES ('Betsy Beyer'), --1
       ('Chris Jones'), --2
       ('Jennifer Petoff'), --3
       ('Niall Richard Murphy'), --4
       ('Lulliana Cosmina'), --5
       ('Rob Harrop'), --6
       ('Chris Schaefer'), --7
       ('Clarence Ho'), --8
       ('Anghel Leonard'), --9
       ('Craig Walls'), --10
       ('Maarten Van Steen'), --11
       ('Andrew S. Tannenbaum'), --12
       ('Benjamin Evans'), --13
       ('Jason Clark'), --14
       ('Martijn Verburg'), --15
       ('Robert Sedgewick'), --16
       ('Wayne Kevin'), --17
       ('Sourabh Sharma'), --18
       ('Sam Newman'); --19

INSERT INTO OTUS_HW_05.GENRES(NAME)
VALUES ('Java'), --1
       ('Algorithms'), --2
       ('Mathematics'), --3
       ('Spring'), --4
       ('Microservices'), --5
       ('Architecture'); --6

INSERT INTO OTUS_HW_05.LNK_BOOKS_AUTHORS(BOOK_ID, AUTHOR_ID)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (2, 5),
       (2, 6),
       (2, 7),
       (2, 8),
       (3, 9),
       (4, 10),
       (5, 11),
       (5, 12),
       (6, 13),
       (6, 14),
       (6, 15),
       (7, 16),
       (7, 17),
       (8, 18),
       (9, 19);

INSERT INTO OTUS_HW_05.LNK_BOOKS_GENRES(BOOK_ID, GENRE_ID)
VALUES (1, 5),
       (1, 6),
       (2, 1),
       (2, 4),
       (3, 1),
       (3, 2),
       (4, 1),
       (4, 4),
       (5, 2),
       (5, 3),
       (5, 6),
       (6, 1),
       (6, 6),
       (7, 1),
       (7, 2),
       (7, 3),
       (8, 1),
       (8, 4),
       (8, 5),
       (9, 6),
       (9, 5);