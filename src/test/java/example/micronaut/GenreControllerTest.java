package example.micronaut;

import example.micronaut.domain.Genre;
import io.micronaut.core.type.Argument;
import io.micronaut.http.*;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest(application = Application.class)
public class GenreControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    //@Inject
    //EntityManager entityManager;

    //@Inject
    //GenreRepositoryImpl model;

    @Test
    public void supplyAnInvalidOrderTriggersValidationFailure() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/genres/list?order=foo"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testFindNonExistingGenreReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/genres/99"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testInsertBeforeTest() {




        //model.save("test-genre-1");

        HttpRequest request;
        HttpResponse response;


        //request = HttpRequest.POST("/genres", new GenreSaveCommand("Microservices"));
        //response = client.toBlocking().exchange(request);


        request = HttpRequest.GET("/genres/list");
        List<Genre> genres = client.toBlocking().retrieve(request, Argument.of(List.class, Genre.class));

        System.out.println("fin");




    }

    @Test
    public void testGenreCrudOperations() {

        List<Long> genreIds = new ArrayList<>();

        HttpRequest request = HttpRequest.POST("/genres", new GenreSaveCommand("DevOps"));
        HttpResponse response = client.toBlocking().exchange(request);
        genreIds.add(entityId(response));

        assertEquals(HttpStatus.CREATED, response.getStatus());

        request = HttpRequest.POST("/genres", new GenreSaveCommand("Microservices"));
        response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());

        Long id = entityId(response);
        genreIds.add(id);
        request = HttpRequest.GET("/genres/" + id);

        Genre genre = client.toBlocking().retrieve(request, Genre.class);

        assertEquals("Microservices", genre.getName());

        request = HttpRequest.PUT("/genres", new GenreUpdateCommand(id, "Micro-services"));
        response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

        request = HttpRequest.GET("/genres/" + id);
        genre = client.toBlocking().retrieve(request, Genre.class);

        System.out.println(genre.getName());

        assertEquals("Micro-services", genre.getName());

        request = HttpRequest.GET("/genres/list");
        List<Genre> genres = client.toBlocking().retrieve(request, Argument.of(List.class, Genre.class));

        assertEquals(2, genres.size());

        request = HttpRequest.POST("/genres/ex", new GenreSaveCommand("Microservices"));
        response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

        request = HttpRequest.GET("/genres/list");
        genres = client.toBlocking().retrieve(request, Argument.of(List.class, Genre.class));

        assertEquals(2, genres.size());

        request = HttpRequest.GET("/genres/list?max=1");
        genres = client.toBlocking().retrieve(request, Argument.of(List.class, Genre.class));

        assertEquals(1, genres.size());
        assertEquals("DevOps", genres.get(0).getName());

        request = HttpRequest.GET("/genres/list?max=1&order=desc&sort=name");
        genres = client.toBlocking().retrieve(request, Argument.of(List.class, Genre.class));

        assertEquals(1, genres.size());
        assertEquals("Micro-services", genres.get(0).getName());

        request = HttpRequest.GET("/genres/list?max=1&offset=10");
        genres = client.toBlocking().retrieve(request, Argument.of(List.class, Genre.class));

        assertEquals(0, genres.size());

        // cleanup:
        for (Long genreId : genreIds) {
            request = HttpRequest.DELETE("/genres/" + genreId);
            response = client.toBlocking().exchange(request);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        }
    }

    protected Long entityId(HttpResponse response) {
        String path = "/genres/";
        String value = response.header(HttpHeaders.LOCATION);
        if (value == null) {
            return null;
        }
        int index = value.indexOf(path);
        if (index != -1) {
            return Long.valueOf(value.substring(index + path.length()));
        }
        return null;
    }
}

