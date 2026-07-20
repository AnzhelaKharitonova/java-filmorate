package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.Collection;
import java.util.Optional;

@Component
public class MpaDbStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE mpa_id = ?";

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    public Collection<Mpa> findAllMpa() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    public Optional<Mpa> findMpaById(int id) {
        try {
            Mpa result = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}
