package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.entity.Comment;

@Repository
public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {
}
