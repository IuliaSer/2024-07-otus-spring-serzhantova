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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.entity.Author;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Genre;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mappers.BookMapper;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@RestController
public class BookController {
    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookMapper bookMapper;

    @GetMapping("/books")
    public Flux<BookDto> getAll() {
        return bookRepository.findAll().map(bookMapper::convertToBookDto);
    }

    @GetMapping("/books/{id}")
    public Mono<BookDto> getById(@PathVariable("id") String id) {
        return getBookById(id)
                .map(bookMapper::convertToBookDto);
    }

    @PutMapping("/books/{id}")
    public Mono<BookDto> update(@PathVariable("id") String id,
                                                @Valid @RequestBody ShortBookDto bookDto) {
        return Mono.zip(getAuthorById(bookDto.getAuthorId()), getGenreById(bookDto.getGenreId()))
                .flatMap(tuple -> getBookById(id)
                        .map(book -> {
                            book.setTitle(bookDto.getTitle());
                            book.setAuthor(tuple.getT1());
                            book.setGenre(tuple.getT2());
                            return book;
                        }))
                .flatMap(bookRepository::save)
                .map(bookMapper::convertToBookDto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/books")
    public Mono<BookDto> create(@Valid @RequestBody ShortBookDto bookDto) {
        return Mono.zip(
                        getAuthorById(bookDto.getAuthorId()),
                        getGenreById(bookDto.getGenreId())
                )
                .flatMap(tuple -> {
                    Book book = new Book(null, bookDto.getTitle(), tuple.getT1(), tuple.getT2());
                    return bookRepository.save(book);
                })
                .map(bookMapper::convertToBookDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/books/{id}")
    public Mono<Void> delete(@PathVariable("id") String id) {
        return bookRepository.deleteById(id);
    }

    private Mono<Book> getBookById(String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(String.format("No book with id %s found", id))));
    }

    private Mono<Author> getAuthorById(String id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(String.format("No book with id %s found", id))));
    }

    private Mono<Genre> getGenreById(String id) {
        return genreRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(String.format("No book with id %s found", id))));
    }
}
