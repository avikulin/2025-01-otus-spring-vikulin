package ru.otus.hw.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.otus.hw.models.contracts.CatalogEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre implements CatalogEntity {
    long id;

    String name;
}
