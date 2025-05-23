package ru.otus.hw.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;

import java.util.List;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestDataUtils {

    public static List<AuthorDto> getAuthorDtos() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new AuthorDto(id.toString(), "Author_" + id)) //ispravit
                .toList();
    }

    public static List<GenreDto> getGenreDtos() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new GenreDto(id.toString(), "Genre_" + id))
                .toList();
    }


    public static List<BookDto> getBookDtos(List<AuthorDto> dbAuthors, List<GenreDto> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new BookDto(id.toString(), "BookTitle_" + id, dbAuthors.get(id - 1), dbGenres.get(id - 1)))
                .toList();
    }

    public static List<BookDto> getBookDtos() {
        List<AuthorDto> dbAuthors = IntStream.range(1, 4).boxed()
                .map(id -> new AuthorDto(id.toString(), "Author_" + id))
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
                .map(id -> new CommentDto(id.toString(), "Message_" + id, dbBooks.get(id - 1)))
                .toList();
    }
}
