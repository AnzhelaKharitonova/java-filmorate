package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmMapper;
    private final GenreRowMapper genreMapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper filmMapper, GenreRowMapper genreMapper) {
        this.jdbc = jdbc;
        this.filmMapper = filmMapper;
        this.genreMapper = genreMapper;
    }

    private static final String FIND_ALL_QUERY = "SELECT f.*, m.mpa_name " +
            "FROM films AS f " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, m.mpa_name " +
            "FROM films AS f " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
            "WHERE f.film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(film_name, description, " +
            "release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_FILM_GENRES_QUERY = "SELECT g.genre_id, g.genre_name " +
            "FROM film_genres AS fg " +
            "JOIN genres AS g ON fg.genre_id = g.genre_id  " +
            "WHERE fg.film_id = ? " +
            "ORDER BY g.genre_id";
    private static final String FIND_ALL_GENRES_QUERY = "SELECT fg.film_id, g.* " +
            "FROM film_genres fg " +
            "JOIN genres g ON fg.genre_id = g.genre_id " +
            "ORDER BY g.genre_id";
    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET film_name = ?, " +
            "description = ?, " +
            "release_date = ?, " +
            "duration = ?, " +
            "mpa_id = ? " +
            "WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY =
            "INSERT INTO likes(film_id, user_id) VALUES(?, ?)";
    private static final String DELETE_LIKE_QUERY =
            "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_POPULAR_FILMS_QUERY = "SELECT f.*, m.mpa_name " +
            "FROM films AS f " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
            "GROUP BY f.film_id, m.mpa_name " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";
    private static final String DELETE_GENRES_FROM_FILM_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String ADD_GENRES_TO_FILM_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbc.query(FIND_ALL_QUERY, filmMapper);
        if (films.isEmpty()) {
            return films;
        }
        return addGenresToFilms(films);
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        try {
            Film result = jdbc.queryForObject(FIND_BY_ID_QUERY, filmMapper, id);
            if (result != null) {
                List<Genre> genresList = jdbc.query(FIND_FILM_GENRES_QUERY, genreMapper, id);
                result.setGenres(new LinkedHashSet<>(genresList));
            }
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Film addFilm(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();

        if (id == null) {
            log.warn("Ошибка в работе с БД");
            throw new InternalServerException("Не удалось сохранить данные");
        }

        film.setId(id);
        saveGenres(film);

        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        int rowsUpdated = jdbc.update(UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId());
        if (rowsUpdated == 0) {
            log.warn("Ошибка в работе с БД");
            throw new InternalServerException("Не удалось обновить данные");
        }
        if (newFilm.getGenres() != null) {
            jdbc.update(DELETE_GENRES_FROM_FILM_QUERY, newFilm.getId());
            saveGenres(newFilm);
        }
        return findFilmById(newFilm.getId()).orElseThrow(() ->
                new InternalServerException("Не удалось обновить данные"));
    }

    @Override
    public Long addLike(Long filmId, Long userId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(ADD_LIKE_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, filmId);
            ps.setLong(2, userId);
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();

        if (id == null) {
            log.warn("Ошибка в работе с БД");
            throw new InternalServerException("Не удалось сохранить данные");
        }
        return userId;
    }

    @Override
    public Long deleteLike(Long id, Long userId) {
        int rowsDeleted = jdbc.update(DELETE_LIKE_QUERY, id, userId);
        if (rowsDeleted == 0) {
            log.warn("Ошибка в работе с БД");
            throw new InternalServerException("Не удалось удалить данные");
        }
        return userId;
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        List<Film> films = jdbc.query(FIND_POPULAR_FILMS_QUERY, filmMapper, count);
        if (films.isEmpty()) {
            return films;
        }
        return addGenresToFilms(films);
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        List<Genre> genresList = new ArrayList<>(film.getGenres());

        jdbc.batchUpdate(ADD_GENRES_TO_FILM_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setInt(2, genresList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genresList.size();
            }
        });
    }

    private List<Film> addGenresToFilms(List<Film> films) {
        Map<Long, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        jdbc.query(FIND_ALL_GENRES_QUERY, (rs) -> {
            Long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);

            if (film != null) {
                Genre genre = new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("genre_name")
                );
                film.getGenres().add(genre);
            }
        });
        return films;
    }

}



