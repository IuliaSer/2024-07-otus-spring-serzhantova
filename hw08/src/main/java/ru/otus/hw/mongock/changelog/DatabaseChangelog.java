package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.entity.Author;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Comment;
import ru.otus.hw.entity.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDB", author = "Iulia Serzhantova", runAlways = true)
    public void dropDb(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "002", id = "init db", author = "Iulia Serzhantova")
    public void initDb(BookRepository bookRepository, AuthorRepository authorRepository,
                            GenreRepository genreRepository, CommentRepository commentRepository) {
        List<Genre> genres = List.of(
                new Genre("1", "Genre_1"),
                new Genre("2", "Genre_2"),
                new Genre("3", "Genre_3"));
        genreRepository.saveAll(genres);

        List<Author> authors = List.of(
                new Author("1", "Author_1"),
                new Author("2", "Author_2"),
                new Author("3", "Author_3"));
        authorRepository.saveAll(authors);

        List<Book> books = List.of(
                new Book("1", "BookTitle_1", authors.get(0), genres.get(0)),
                new Book("2", "BookTitle_2", authors.get(1), genres.get(1)),
                new Book("3", "BookTitle_3", authors.get(2), genres.get(2)));
        bookRepository.saveAll(books);

        List<Comment> comments = List.of(
                new Comment("1", "Comment_1", books.get(0)),
                new Comment("2", "Comment_2", books.get(1)),
                new Comment("3", "Comment_3", books.get(2)));
        commentRepository.saveAll(comments);
    }
}
