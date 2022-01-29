package example.micronaut;

import example.micronaut.domain.Book;
import example.micronaut.domain.Genre;
//import io.micronaut.data.annotation.Repository;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaSpecificationExecutor;
import io.micronaut.data.repository.GenericRepository;
import io.micronaut.transaction.annotation.TransactionalAdvice;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import io.micronaut.transaction.annotation.ReadOnly;
import org.hibernate.Session;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton
@Repository(value = "first")
abstract class GenreRepositoryImpl implements GenericRepository<Genre, Long> {
    @Inject
    @PersistenceContext(name = "first")
    private EntityManager entityManager;
    private final ApplicationConfiguration applicationConfiguration;



    private final static List<String> VALID_PROPERTY_NAMES = Arrays.asList("id", "name");

    public GenreRepositoryImpl(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }


    @ReadOnly
    @Transactional
    @TransactionalAdvice("first")
    public Optional<Genre> findById(@NotNull Long id) {
        return Optional.ofNullable(entityManager.find(Genre.class, id));
    }


    @Transactional
    @TransactionalAdvice("first")
    public Genre save(@NotBlank String name) {
        Genre genre = new Genre(name);
        entityManager.persist(genre);
        return genre;
    }


    @Transactional
    @TransactionalAdvice("first")
    public void deleteById(@NotNull Long id) {
        findById(id).ifPresent(entityManager::remove);
    }

    @ReadOnly
    @Transactional
    @TransactionalAdvice("first")
    public List<Genre> findAll(@NotNull SortingAndOrderArguments args) {
        String qlString = "SELECT g FROM Genre as g";
        if (args.getOrder().isPresent() && args.getSort().isPresent() && VALID_PROPERTY_NAMES.contains(args.getSort().get())) {
            qlString += " ORDER BY g." + args.getSort().get() + " " + args.getOrder().get().toLowerCase();
        }
        TypedQuery<Genre> query = entityManager.createQuery(qlString, Genre.class);
        query.setMaxResults(args.getMax().orElseGet(applicationConfiguration::getMax));
        args.getOffset().ifPresent(query::setFirstResult);

        return query.getResultList();
    }


    @Transactional
    @TransactionalAdvice("first")
    public int update(@NotNull Long id, @NotBlank String name) {
        return entityManager.createQuery("UPDATE Genre g SET name = :name where id = :id")
                .setParameter("name", name)
                .setParameter("id", id)
                .executeUpdate();
    }


    @Transactional
    @TransactionalAdvice("first")
    public Genre saveWithException(@NotBlank String name) {
        save(name);
        throw new PersistenceException();
    }
}
