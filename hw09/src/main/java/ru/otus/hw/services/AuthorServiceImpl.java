package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.services.contracts.AuthorService;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public List<Author> findAllByIds(Set<Long> ids) {
        return authorRepository.findAllByIds(ids);
    }

    @Override
    public Author findById(long id) {
        return authorRepository.findById(id).orElseThrow();
    }
}
