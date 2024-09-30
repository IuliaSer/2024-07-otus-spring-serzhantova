package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.entity.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.hibernate.jpa.QueryHints.JAKARTA_HINT_FETCH_GRAPH;
import static ru.otus.hw.utils.Constants.BOOKS_ENTITY_GRAPH;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAKARTA_HINT_FETCH_GRAPH, getEntityGraph());

        return Optional.ofNullable(em.find(Book.class, id, properties));
    }

    @Override
    public List<Book> findAll() {
        return em.createQuery("select book from Book book", Book.class)
                .setHint(JAKARTA_HINT_FETCH_GRAPH, getEntityGraph())
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        return em.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Book book = em.find(Book.class, id);
        if (nonNull(book)) {
            em.remove(book);
        }
    }

    private EntityGraph<?> getEntityGraph() {
        return em.getEntityGraph(BOOKS_ENTITY_GRAPH);
    }
}