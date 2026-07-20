package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.Optional;

@Component
public class GenreDbStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    public Collection<Genre> findAllGenres() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    public Optional<Genre> findGenreById(int id) {
        try {
            Genre result = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

}
