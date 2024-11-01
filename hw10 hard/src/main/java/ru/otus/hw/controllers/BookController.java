package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookDtoIds;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/")
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDto getById(@PathVariable("id") long id) {
        BookDto book = bookService.findById(id);
        return book;
    }

    @PatchMapping("/update")
    public BookDto update(@Valid @RequestBody BookDtoIds book, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            return "update";
//        }

        return bookService.update(book.getId(), book.getTitle(), book.getAuthorId(), book.getGenreId());
    }

    @PostMapping("/create")
    public BookDto create(@RequestBody BookDtoIds book) {
        return bookService.insert(book.getTitle(), book.getAuthorId(), book.getGenreId());
    }

    @PostMapping("/delete/{id}")
    public void delete(@PathVariable("id") long id) {
        bookService.deleteById(id);
    }
}
