package ru.otus.hw.repositories;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcGenreRepository implements GenreRepository {

    private final JdbcOperations jdbcOperations;

    private final NamedParameterJdbcOperations namedParameterJdbc;

    public JdbcGenreRepository(NamedParameterJdbcOperations namedParameterJdbc) {
        this.jdbcOperations = namedParameterJdbc.getJdbcOperations();
        this.namedParameterJdbc = namedParameterJdbc;
    }

    @Override
    public List<Genre> findAll() {
        return jdbcOperations.query("select id, name from genres", new GenreRowMapper());
    }

    @Override
    public Optional<Genre> findById(long id) {
        return Optional.ofNullable(namedParameterJdbc.queryForObject("select id, name from genres where id = :id",
                Map.of("id", id), new GenreRowMapper()));
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Genre(id, name);
        }
    }
}