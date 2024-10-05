package ru.otus.hw.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.entity.Author;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Comment;
import ru.otus.hw.entity.Genre;

import java.util.List;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestDataUtils {

    public static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

    public static List<Genre> getDbGenres() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    public static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id, "BookTitle_" + id, dbAuthors.get(id - 1), dbGenres.get(id - 1)))
                .toList();
    }

    public static List<Book> getDbBooks() {
        List<Author> dbAuthors = IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }

    public static List<Comment> getDbComments() {
        List<Book> dbBooks = getDbBooks();
        return getDbComments(dbBooks);
    }

    public static List<Comment> getDbComments(List<Book> dbBooks) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Comment(id, "Message_" + id, dbBooks.get(id - 1)))
                .toList();
    }

    public static List<AuthorDto> getAuthorDtos() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new AuthorDto(id, "Author_" + id))
                .toList();
    }

    public static List<GenreDto> getGenreDtos() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new GenreDto(id, "Genre_" + id))
                .toList();
    }


    public static List<BookDto> getBookDtos(List<AuthorDto> dbAuthors, List<GenreDto> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new BookDto(id, "BookTitle_" + id, dbAuthors.get(id - 1), dbGenres.get(id - 1)))
                .toList();
    }

    public static List<BookDto> getBookDtos() {
        List<AuthorDto> dbAuthors = IntStream.range(1, 4).boxed()
                .map(id -> new AuthorDto(id, "Author_" + id))
                .toList();
        var dbGenres = getGenreDtos();
        return getBookDtos(dbAuthors, dbGenres);
    }

    public static List<CommentDto> getCommentDtos() {
        List<BookDto> dbBooks = getBookDtos();
        return getCommentDtos(dbBooks);
    }

    public static List<CommentDto> getCommentDtos(List<BookDto> dbBooks) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new CommentDto(id, "Message_" + id, dbBooks.get(id - 1)))
                .toList();
    }
}
