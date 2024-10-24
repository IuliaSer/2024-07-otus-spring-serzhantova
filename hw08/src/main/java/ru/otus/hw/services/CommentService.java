package ru.otus.hw.services;

import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<CommentDto> findById(String id);

    @Transactional(readOnly = true)
    List<CommentDto> findAllByBookId(String bookId);

    @Transactional
    CommentDto insert(String message, String bookId);

    @Transactional
    CommentDto update(String id, String message, String bookId);

    void deleteById(String id);
}
