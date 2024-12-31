package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.entity.Book;

import java.util.List;
import java.util.Optional;

import static ru.otus.hw.utils.Constants.BOOKS_ENTITY_GRAPH;

@RepositoryRestResource(path = "books")
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(value = BOOKS_ENTITY_GRAPH)
    @RestResource(path = "id", rel = "id")
    Optional<Book> findById(long id);

    @EntityGraph(value = BOOKS_ENTITY_GRAPH)
    List<Book> findAll();
}