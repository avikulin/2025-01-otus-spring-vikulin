package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.otus.hw.models.contracts.CatalogEntity;

import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "BOOKS")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
/*@NamedEntityGraph(name = "book-aggregate",
                  attributeNodes = {@NamedAttributeNode("authors"),
                                    @NamedAttributeNode("genres"),
                                    @NamedAttributeNode("comments")})*/
public class Book implements CatalogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude // канает только бизне-ключ
    long id;

    @Column(name = "TITLE", nullable = false)
    String title;

    @Column(name = "YEAR_OF_PUBLISHED", nullable = false)
    int yearOfPublished;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "LNK_BOOKS_AUTHORS",
               joinColumns = {@JoinColumn(name = "BOOK_ID")} ,
               inverseJoinColumns = {@JoinColumn(name = "AUTHOR_ID")})
    @Fetch(FetchMode.SUBSELECT)
    @EqualsAndHashCode.Exclude  // канает только бизне-ключ
    @ToString.Exclude // чтобы не тащить из БД на каждом запросе
    Set<Author> authors;


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "LNK_BOOKS_GENRES",
            joinColumns = {@JoinColumn(name = "BOOK_ID")} ,
            inverseJoinColumns = {@JoinColumn(name = "GENRE_ID")})
    @Fetch(FetchMode.SUBSELECT)
    @EqualsAndHashCode.Exclude  // канает только бизне-ключ
    @ToString.Exclude // чтобы не тащить из БД на каждом запросе
    Set<Genre> genres;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "BOOK_ID")
    @Fetch(FetchMode.SUBSELECT)
    Set<Comment> comments;
}
