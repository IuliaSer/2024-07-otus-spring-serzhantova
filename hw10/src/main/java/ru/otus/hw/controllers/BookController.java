package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.services.BookService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookController {
    private final BookService bookService;

    @GetMapping("/books")
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/books/{id}")
    public BookDto getById(@PathVariable("id") long id) {
        return bookService.findById(id);
    }

    @PutMapping("/books")
    public BookDto update(@Valid @RequestBody ShortBookDto book) {
        return bookService.update(book.getId(), book.getTitle(), book.getAuthorId(), book.getGenreId());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/books")
    public BookDto create(@Valid @RequestBody ShortBookDto book) {
        return bookService.insert(book.getTitle(), book.getAuthorId(), book.getGenreId());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/books/{id}")
    public void delete(@PathVariable("id") long id) {
        bookService.deleteById(id);
    }
}
