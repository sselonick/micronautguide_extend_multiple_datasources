package example.micronaut.other;

import example.micronaut.domain.Genre;
import io.micronaut.core.annotation.Introspected;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
@Introspected
@Entity
@Table(name = "other_book")
public class OtherBook {

    public OtherBook() {}

    public OtherBook(@NotNull String name) {
        this.name = name;

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name +
                '}';
    }
}