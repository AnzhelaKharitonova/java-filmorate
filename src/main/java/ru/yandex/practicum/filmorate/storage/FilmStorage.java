package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film findFilmById(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    boolean validateName(String name, boolean isNewFilm);

    boolean validateDescription(String description);

    boolean validateReleaseDate(LocalDate releaseDate);

    boolean validateDuration(Integer duration);

    void validateId(Long id);

    Long generateId();
}
