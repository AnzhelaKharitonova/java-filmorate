package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.sql.Statement;
import java.util.*;

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

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(title, description, release_date, duration, rating)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String ADD_FILM_GENRES_QUERY = "INSERT INTO film_genres(film_id, genre_id)" +
            "VALUES (?, ?)";
    private static final String FIND_FILM_GENRES_QUERY = "SELECT g.genre_id, g.genre_name FROM genres AS g " +
            "JOIN film_genres AS fg ON g.genre_id = fg.genre_id  " +
            "WHERE fg.film_id = ?";
    private static final String UPDATE_QUERY =
            "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, rating = ? WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY =
            "INSERT INTO likes(film_id, user_id) VALUES(?, ?)";
    private static final String DELETE_LIKE_QUERY =
            "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_LIKES_QUERY =
            "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String FIND_POPULAR_FILMS_QUERY =
            "SELECT f.* " +
                    "FROM films AS f " +
                    "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT ?";


    @Override
    public Collection<Film> findAll() {
        return jdbc.query(FIND_ALL_QUERY, filmMapper);
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        try {
            Film result = jdbc.queryForObject(FIND_BY_ID_QUERY, filmMapper, id);
            List<Genre> filmGenres = jdbc.query(FIND_FILM_GENRES_QUERY, new GenreRowMapper(), id);
            result.setGenres(filmGenres);
            result.setLikes(findLikes(id));
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
            ps.setString(5, film.getRating().getName());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();

        if (id == null) {
            log.warn("Ошибка в работе с БД");
            throw new InternalServerException("Не удалось сохранить данные");
        }

        film.setId(id);
        addFilmGenres(film);

        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) { //todo добавить жанры
        int rowsUpdated = jdbc.update(UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getRating().getName(),
                newFilm.getId());
        if (rowsUpdated == 0) {
            log.warn("Ошибка в работе с БД");
            throw new InternalServerException("Не удалось обновить данные");
        }
        addFilmGenres(newFilm);
        return jdbc.queryForObject(FIND_BY_ID_QUERY, filmMapper, newFilm.getId());
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
    public List<Film> findPopularFilms(Long count) {
        return jdbc.query(FIND_POPULAR_FILMS_QUERY, filmMapper, count);
    }

    private void addFilmGenres(Film film) {
        List<Genre> filmgenres = film.getGenres();
        if (filmgenres == null) return;
        for (Genre filmGenre : filmgenres) {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(ADD_FILM_GENRES_QUERY, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, film.getId());
                ps.setLong(2, filmGenre.getId());
                return ps;
            }, keyHolder);

            Long id = keyHolder.getKey().longValue();

            if (id == null) {
                log.warn("Ошибка в работе с БД");
                throw new InternalServerException("Не удалось сохранить данные");
            }
        }
    }

    private Set<Long> findLikes(Long filmId) {
        List<Long> likes = jdbc.queryForList(FIND_LIKES_QUERY, Long.class, filmId);
        return new HashSet<>(likes);

    }

}



