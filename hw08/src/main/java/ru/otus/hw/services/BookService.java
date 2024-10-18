package ru.otus.hw.services;

import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<BookDto> findById(String id);

    List<BookDto> findAll();


    @Transactional
    BookDto insert(String title, String authorId, String genreId);

    @Transactional
    BookDto update(String id, String title, String authorId, String genreId);

    void deleteById(String id);

}