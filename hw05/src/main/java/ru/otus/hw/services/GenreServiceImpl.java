package ru.otus.hw.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.contracts.GenreRepository;
import ru.otus.hw.services.contracts.GenreService;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreServiceImpl implements GenreService {

    GenreRepository genreRepository;

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        return genreRepository.findAllByIds(ids);
    }
}
