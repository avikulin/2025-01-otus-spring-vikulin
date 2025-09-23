package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.EntityValidationException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.base.BaseBookRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends BaseBookRepository {
    default Optional<Book> findByIdWithCheck(long id) {
        if (id < 1) {
            throw new EntityValidationException("Trying to process impossible value as ID for find operation");
        }
        var item = this.findById(id);
        if (item.isEmpty()) {
            throw new EntityNotFoundException("The book with ID = %d is not found in the DB".formatted(id));
        }
        return item;
    }

    default List<Book> findAllBatched(){
        var data = findAll();
        if (!data.isEmpty()) {
            // заставляем JPA прогрузить все батчи.
            data.forEach(item->{
                item.getAuthors().size();
                item.getGenres().size();
                item.getComments().size();
            });
        }
        return data;
    }

    @Modifying
    @Query("DELETE FROM Book b WHERE b.id = :id")
    int deleteByIdWithCheck(@Param("id") Long id);

    default void deleteById(Long id) {
        if (id < 1) {
            throw new EntityValidationException("Trying to process impossible value as ID for delete operation");
        }
        var rowsAffected = this.deleteByIdWithCheck(id);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("The book with ID = %d is not found in the DB".formatted(id));
        }
    }
}
