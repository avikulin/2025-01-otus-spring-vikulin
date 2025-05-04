package ru.otus.hw.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.otus.hw.models.contracts.CatalogEntity;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "BOOKS")
@NoArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NamedEntityGraph(name = "book-aggregate",
                  attributeNodes = {
                        @NamedAttributeNode(value = "authors", subgraph = "authors-subgraph"),
                        @NamedAttributeNode(value = "genres", subgraph = "genres-subgraph"),
                        @NamedAttributeNode(value = "comments", subgraph = "comments-subgraph")
                  },
                  subgraphs = {
                        @NamedSubgraph(
                            name = "authors-subgraph",
                            attributeNodes = @NamedAttributeNode("fullName")
                        ),
                        @NamedSubgraph(
                            name = "genres-subgraph",
                            attributeNodes = @NamedAttributeNode("name")
                        ),
                        @NamedSubgraph(
                            name = "comments-subgraph",
                            attributeNodes = @NamedAttributeNode("text")
                        )
                  }
)
@DynamicUpdate // чтобы не тащить из БД все коллекции при апдейте рута
public class Book implements CatalogEntity {
    @Id
    @NonNull //некрасиво, но необходимо для RequiredArgsConstructor
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude // канает только бизнес-ключ
    long id;

    @NonNull //некрасиво, но необходимо для RequiredArgsConstructor
    @Column(name = "TITLE", nullable = false)
    String title;

    @NonNull //некрасиво, но необходимо для RequiredArgsConstructor
    @Column(name = "YEAR_OF_PUBLISHED", nullable = false)
    int yearOfPublished;

    @ManyToMany(targetEntity = Author.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "LNK_BOOKS_AUTHORS",
               joinColumns = {@JoinColumn(name = "BOOK_ID")} ,
               inverseJoinColumns = {@JoinColumn(name = "AUTHOR_ID")})
    @BatchSize(size = 10) // по хорошему надо бы 100 - но для примера пойдет и так....
    @EqualsAndHashCode.Exclude  // канает только бизнес-ключ
    @ToString.Exclude // чтобы не тащить из БД на каждом запросе
    Set<Author> authors = new HashSet<>(); //инициализируем статически. В конструкторах этого параметра не будет.


    @ManyToMany(targetEntity = Genre.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "LNK_BOOKS_GENRES",
            joinColumns = {@JoinColumn(name = "BOOK_ID")} ,
            inverseJoinColumns = {@JoinColumn(name = "GENRE_ID")})
    @BatchSize(size = 10) // по хорошему надо бы 100 - но для примера пойдет и так....
    @EqualsAndHashCode.Exclude  // канает только бизнес-ключ
    @ToString.Exclude // чтобы не тащить из БД на каждом запросе
    Set<Genre> genres = new HashSet<>(); //инициализируем статически. В конструкторах этого параметра не будет.

    // при удалении книги - комменты хранить тоже смысла нет.
    @OneToMany(targetEntity = Comment.class, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "BOOK_ID")
    @BatchSize(size = 10) // по хорошему надо бы 100 - но для примера пойдет и так....
    @OnDelete(action = OnDeleteAction.CASCADE)
    @EqualsAndHashCode.Exclude  // канает только бизнес-ключ
    @ToString.Exclude // чтобы не тащить из БД на каждом запросе
    Set<Comment> comments = new HashSet<>(); //инициализируем статически. В конструкторах этого параметра не будет.
}
