package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Optional<Film> findFilmById(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    Long addLike(Long filmId, Long userId);

    Long deleteLike(Long id, Long userId);

    List<Film> findPopularFilms(Long count);
}
