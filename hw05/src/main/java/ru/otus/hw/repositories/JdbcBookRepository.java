package ru.otus.hw.repositories;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcBookRepository implements BookRepository {

    private final JdbcOperations jdbcOperations;

    private final NamedParameterJdbcOperations namedParameterJdbc;

    public JdbcBookRepository(NamedParameterJdbcOperations namedParameterJdbc) {
        this.jdbcOperations = namedParameterJdbc.getJdbcOperations();
        this.namedParameterJdbc = namedParameterJdbc;
    }

    @Override
    public Optional<Book> findById(long id) {
        String sqlQuery = "select b.id as book_id, b.title as book_title, " +
                "a.id as author_id, a.full_name as author_full_name, " +
                "g.id as genre_id, g.name as genre_name " +
                "from books b " +
                "join authors a on b.author_id = a.id " +
                "join genres g on b.genre_id = g.id " +
                "where b.id = :id";
        return namedParameterJdbc.query(sqlQuery, Map.of("id", id), new BookRowMapper()).stream().findFirst();
    }

    @Override
    public List<Book> findAll() {
        String sqlQuery = "select b.id as book_id, b.title as book_title, " +
                "a.id as author_id, a.full_name as author_full_name, " +
                "g.id as genre_id, g.name as genre_name " +
                "from books b " +
                "join authors a on b.author_id = a.id " +
                "join genres g on b.genre_id = g.id ";
        return jdbcOperations.query(sqlQuery, new BookRowMapper());
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        namedParameterJdbc.update("delete from books where id = :id", Map.of("id", id));
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("genre_id", book.getGenre().getId());

        namedParameterJdbc.update("insert into books (id, title, author_id, genre_id) " +
                        "values (default, :title, :author_id, :genre_id)", params, keyHolder, new String[]{"id"});
        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        return book;
    }

    private Book update(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("genre_id", book.getGenre().getId());

        int numberOfRowsUpdated = namedParameterJdbc.update(
                "update books set title = :title, " +
                        "author_id = :author_id, " +
                        "genre_id = :genre_id",
                params);

        if (numberOfRowsUpdated == 0) {
            throw new EntityNotFoundException(String.format("Не получилось обновить книгу с id: %s", book.getId()));
        }

        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong("book_id");
            String bookTitle = rs.getString("book_title");
            long authorId = rs.getLong("author_id");
            String authorFullName = rs.getString("author_full_name");
            long genreId = rs.getLong("genre_id");
            String genreName = rs.getString("genre_name");
            return new Book(bookId, bookTitle, new Author(authorId, authorFullName), new Genre(genreId, genreName));
        }
    }
}