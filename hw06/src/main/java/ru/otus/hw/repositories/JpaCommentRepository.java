package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.entity.Comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.hibernate.jpa.QueryHints.JAKARTA_HINT_FETCH_GRAPH;
import static ru.otus.hw.utils.Constants.COMMENTS_ENTITY_GRAPH;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAKARTA_HINT_FETCH_GRAPH, getEntityGraph());

        return Optional.ofNullable(em.find(Comment.class, id, properties));
    }

    @Override
    public List<Comment> findAllByBookId(long bookId) {
        TypedQuery<Comment> query = em.createQuery("select c from Comment c where c.book.id = :bookId",
                Comment.class);
        return query
                .setParameter("bookId", bookId)
                .setHint(JAKARTA_HINT_FETCH_GRAPH, getEntityGraph())
                .getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        Comment comment = em.find(Comment.class, id);
        if (nonNull(comment)) {
            em.remove(comment);
        }
    }

    private EntityGraph<?> getEntityGraph() {
        return em.getEntityGraph(COMMENTS_ENTITY_GRAPH);
    }

}
