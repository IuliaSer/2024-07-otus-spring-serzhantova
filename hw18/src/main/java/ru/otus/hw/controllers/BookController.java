package ru.otus.hw.controllers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.exceptions.TooManyRequestsException;
import ru.otus.hw.services.BookService;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @RateLimiter(name = "rateLimiter", fallbackMethod = "rateLimiterFallback")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "fallback")
    @GetMapping
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDto getById(@PathVariable("id") long id) {
        return bookService.findById(id);
    }

    @PutMapping
    public BookDto update(@Valid @RequestBody ShortBookDto book) {
        return bookService.update(book.getId(), book.getTitle(), book.getAuthorId(), book.getGenreId());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookDto create(@Valid @RequestBody ShortBookDto book) {
        return bookService.insert(book.getTitle(), book.getAuthorId(), book.getGenreId());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        bookService.deleteById(id);
    }

    private List<BookDto> fallback(Exception e) {
        log.error("Отработал Circuit Breaker: {}", e.getMessage());
        return Collections.emptyList();
    }

    private List<BookDto> rateLimiterFallback(Exception e) {
        log.warn("Превышен лимит запросов: {}", e.getMessage());
        throw new TooManyRequestsException("Превышен лимит запросов. Попробуйте позже");
    }
}
