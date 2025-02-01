package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto findById(long id);

    List<CommentDto> findAllByBookId(long bookId);

    CommentDto insert(String message, long bookId);

    CommentDto update(long id, String message, long bookId);

    void deleteById(long id);
}
