package example.micronaut;

import example.micronaut.domain.Genre;
import example.micronaut.other.OtherBook;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaSpecificationExecutor;
import io.micronaut.data.repository.GenericRepository;
import io.micronaut.transaction.annotation.TransactionalAdvice;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;


@Singleton
@Repository(value = "other")
abstract class OtherBookRepositoryImpl implements GenericRepository<OtherBook, Long> {

    @Inject
    @PersistenceContext(name = "other")
    private EntityManager entityManager;
    private final ApplicationConfiguration applicationConfiguration;


    //public OtherBookRepositoryImpl(EntityManager entityManager,
    //                               ApplicationConfiguration applicationConfiguration) {
    //    this.entityManager = entityManager;
    //    this.applicationConfiguration = applicationConfiguration;
    //}
    public OtherBookRepositoryImpl(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }





    @Transactional
    @TransactionalAdvice("other")
    public OtherBook save(@NotBlank String name) {
        OtherBook otherBook = new OtherBook(name);
        entityManager.persist(otherBook);
        return otherBook;
    }


}
