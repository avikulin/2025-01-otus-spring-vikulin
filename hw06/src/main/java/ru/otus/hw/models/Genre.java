package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.otus.hw.models.contracts.CatalogEntity;

@Data
@Entity
@Table(name = "GENRES")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre implements CatalogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude // канает только бизне-ключ
    long id;

    @Column(name="NAME", nullable = false)
    String name;
}
