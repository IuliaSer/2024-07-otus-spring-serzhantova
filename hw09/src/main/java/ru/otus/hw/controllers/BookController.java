package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/")
    public String getAll(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable("id") long id, Model model) {
        BookDto book = bookService.findById(id);
        model.addAttribute("book", book);
        return "book";
    }

    @GetMapping("/update/{id}")
    public String update(@PathVariable("id") long id, Model model) {
        BookDto book = bookService.findById(id);
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "update";
    }

    @PostMapping("/update")
    public String save(@Valid @ModelAttribute("book") ShortBookDto book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "update";
        }

        bookService.update(book.getId(), book.getTitle(), book.getAuthorId(), book.getGenreId());
        return "redirect:/books/";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("book", new ShortBookDto());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("book") ShortBookDto book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "create";
        }
        bookService.insert(book.getTitle(), book.getAuthorId(), book.getGenreId());
        return "redirect:/books/";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") long id) {
        bookService.deleteById(id);
        return "redirect:/books/";
    }
}
