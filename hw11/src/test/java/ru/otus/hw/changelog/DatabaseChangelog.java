package ru.otus.hw.changelog;

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

    private List<Author> authors;

    private List<Book> books;

    private List<Comment> comments;

    private List<Genre> genres;

    @ChangeSet(order = "001", id = "dropDB", author = "Iulia Serzhantova", runAlways = true)
    public void dropDb(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "002", id = "init genres", author = "Iulia Serzhantova")
    public void initGenres(GenreRepository genreRepository) {
        genres = List.of(
                new Genre("1", "Genre_1"),
                new Genre("2", "Genre_2"),
                new Genre("3", "Genre_3"));
        genreRepository.saveAll(genres).blockLast();
    }

    @ChangeSet(order = "003", id = "init authors", author = "Iulia Serzhantova")
    public void initAuthors(AuthorRepository authorRepository) {
        authors = List.of(
                new Author("1", "Author_1"),
                new Author("2", "Author_2"),
                new Author("3", "Author_3"));
        authorRepository.saveAll(authors).blockLast();
    }

    @ChangeSet(order = "004", id = "init books", author = "Iulia Serzhantova")
    public void initBooks(BookRepository bookRepository) {
        books = List.of(
                new Book("1", "BookTitle_1", authors.get(0), genres.get(0)),
                new Book("2", "BookTitle_2", authors.get(1), genres.get(1)),
                new Book("3", "BookTitle_3", authors.get(2), genres.get(2)));
        bookRepository.saveAll(books).blockLast();

    }

    @ChangeSet(order = "005", id = "init comments", author = "Iulia Serzhantova")
    public void initComments(CommentRepository commentRepository) {
        comments = List.of(
                new Comment("1", "Comment_1", books.get(0)),
                new Comment("2", "Comment_2", books.get(1)),
                new Comment("3", "Comment_3", books.get(2)));
        commentRepository.saveAll(comments).blockLast();
    }
}
