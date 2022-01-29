package example.micronaut;

import example.micronaut.other.OtherBook;

import io.micronaut.data.repository.GenericRepository;
import io.micronaut.transaction.annotation.TransactionalAdvice;

import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;


public interface OtherBookRepository extends GenericRepository<OtherBook, Long> {

    @Transactional
    @TransactionalAdvice("other")
    OtherBook save(@NotBlank String name);

}