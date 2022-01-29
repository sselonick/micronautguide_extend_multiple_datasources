package example.micronaut;

import example.micronaut.domain.Genre;
//import io.micronaut.data.annotation.Repository;
//import io.micronaut.data.repository.GenericRepository;
import io.micronaut.data.repository.GenericRepository;
import io.micronaut.transaction.annotation.TransactionalAdvice;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;


public interface GenreRepository {

    @TransactionalAdvice("first")
    Optional<Genre> findById(@NotNull Long id);

    @TransactionalAdvice("first")
    Genre save(@NotBlank String name);

    @TransactionalAdvice("first")
    Genre saveWithException(@NotBlank String name);

    @TransactionalAdvice("first")
    void deleteById(@NotNull Long id);

    @TransactionalAdvice("first")
    List<Genre> findAll(@NotNull SortingAndOrderArguments args);

    @TransactionalAdvice("first")
    int update(@NotNull Long id, @NotBlank String name);
}