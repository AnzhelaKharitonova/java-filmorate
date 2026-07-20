package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Getter
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

    @Autowired
    public FilmService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage,
                       GenreDbStorage genreStorage, MpaDbStorage mpaStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film addFilm(Film film) {
        validateName(film.getName(), true);
        validateDescription(film.getDescription());
        validateReleaseDate(film.getReleaseDate());
        validateDuration(film.getDuration());
        validateMpa(film.getMpa());
        validateGenres(film.getGenres());

        Film savedFilm = filmStorage.addFilm(film);
        log.info("Добавлен новый фильм с id = {}", savedFilm.getId());
        return savedFilm;
    }

    public Film findFilmById(Long id) {
        return filmStorage.findFilmById(id).orElseThrow(() ->
                new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    public Film updateFilm(Film film) {
        validateId(film.getId());
        Film updatedFilm = findFilmById(film.getId());
        if (validateName(film.getName(), false)) {
            updatedFilm.setName(film.getName());
        }
        if (validateDescription(film.getDescription())) {
            updatedFilm.setDescription(film.getDescription());
        }
        if (validateReleaseDate(film.getReleaseDate())) {
            updatedFilm.setReleaseDate(film.getReleaseDate());
        }
        if (validateDuration(film.getDuration())) {
            updatedFilm.setDuration(film.getDuration());
        }
        if (validateMpa(film.getMpa())) {
            updatedFilm.setMpa(film.getMpa());
        }
        if (validateGenres(film.getGenres())) {
            updatedFilm.setGenres(film.getGenres());
        }

        filmStorage.updateFilm(updatedFilm);
        log.info("Обновлены данные о фильме с id = {}", updatedFilm.getId());
        return updatedFilm;
    }

    public Long addLike(Long id, Long userId) {
        findFilmById(id);
        findUserById(userId);
        filmStorage.addLike(id, userId);
        log.info("Фильму с id = {} добавлен лайк от пользователя с id = {}", id, userId);
        return userId;
    }

    public Long deleteLike(Long id, Long userId) {
        findFilmById(id);
        findUserById(userId);
        filmStorage.deleteLike(id, userId);
        log.info("У фильма с id = {} удален лайк от пользователя с id = {}", id, userId);
        return userId;
    }

    public List<Film> findPopularFilms(int count) {
        return filmStorage.findPopularFilms(count);
    }

    private User findUserById(Long id) {
        return userStorage.findUserById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    private boolean validateMpa(Mpa mpa) {
        if (mpa == null) return false;
        mpaStorage.findMpaById(mpa.getId()).orElseThrow(() ->
                new NotFoundException("Рейтинг с id = " + mpa.getId() + " не найден"));
        return true;
    }

    private boolean validateGenres(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) return false;
        for (Genre genre : genres) {
            genreStorage.findGenreById(genre.getId()).orElseThrow(() ->
                    new NotFoundException("Жанр с id = " + genre.getId() + " не найден"));
        }
        return true;
    }

    private boolean validateName(String name, boolean isNewFilm) {
        if (name == null) {
            if (isNewFilm) {
                log.warn("Ошибка валидации названия фильма");
                throw new ValidationException("Поле название фильма должно быть заполнено");
            }
            return false;
        }
        if (name.isBlank()) {
            log.warn("Ошибка валидации названия фильма");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        return true;
    }

    private boolean validateDescription(String description) {
        if (description == null) {
            return false;
        }
        if (description.length() > 200) {
            log.warn("Ошибка валидации описания фильма");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        return true;
    }

    private boolean validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate == null) {
            return false;
        }
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка валидации даты релиза фильма");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (releaseDate.isAfter(LocalDate.now().plusYears(1))) {
            log.warn("Ошибка валидации даты релиза фильма");
            throw new ValidationException("Дата релиза фильма не может быть больше чем текущий либо следующий год");
        }
        return true;
    }

    private boolean validateDuration(Integer duration) {
        if (duration == null) {
            return false;
        }
        if (duration <= 0) {
            log.warn("Ошибка валидации продолжительности фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        return true;
    }

    private void validateId(Long id) {
        if (id == null) {
            log.warn("Ошибка валидации id");
            throw new ValidationException(("Поле id не может быть пустым"));
        }
    }

}
