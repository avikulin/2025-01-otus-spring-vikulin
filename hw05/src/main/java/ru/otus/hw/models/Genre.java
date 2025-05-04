package ru.otus.hw.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
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
