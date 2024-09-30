package ru.otus.hw.services;

import ru.otus.hw.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(long id);

    List<Comment> findAllByBookId(long bookId);

    Comment insert(String message, long bookId);

    Comment update(long id, String message, long bookId);

    void deleteById(long id);
}
