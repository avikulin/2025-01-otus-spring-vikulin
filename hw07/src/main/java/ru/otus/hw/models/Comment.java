package ru.otus.hw.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.otus.hw.models.contracts.CatalogEntity;

@Data
@Entity
@Table(name = "COMMENTS")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment implements CatalogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude // канает только бизнес-ключ
    long id;

    @NonNull //некрасиво, но необходимо для RequiredArgsConstructor
    @Column(name = "BOOK_ID", nullable = false)
    long bookId;

    @NonNull //некрасиво, но необходимо для RequiredArgsConstructor
    @Column(name = "COMMENT_TEXT", nullable = false)
    String text;
}
