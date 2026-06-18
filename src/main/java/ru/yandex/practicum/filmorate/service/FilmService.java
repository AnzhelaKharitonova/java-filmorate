package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Getter
public class FilmService {

    private final Map<Integer, Film> films;

    public FilmService() {
        films = new HashMap<>();
    }

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film addFilm(Film film) throws ValidationException {
        validateName(film.getName(), true);
        validateDescription(film.getDescription());
        validateReleaseDate(film.getReleaseDate());
        validateDuration(film.getDuration());

        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм с id = {}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) throws ValidationException {
        validateId(film.getId());

        Film updatableFilm = films.get(film.getId());
        if (validateName(film.getName(), false)) {
            updatableFilm.setName(film.getName());
        }
        if (validateDescription(film.getDescription())) {
            updatableFilm.setDescription(film.getDescription());
        }
        if (validateReleaseDate(film.getReleaseDate())) {
            updatableFilm.setReleaseDate(film.getReleaseDate());
        }
        if (validateDuration(film.getDuration())) {
            updatableFilm.setDuration(film.getDuration());
        }
        films.put(updatableFilm.getId(), updatableFilm);
        log.info("Обновлен фильм с id = {}", film.getId());
        return updatableFilm;
    }

    private boolean validateName(String name, boolean isNewFilm) throws ValidationException {
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

    private boolean validateDescription(String description) throws ValidationException {
        if (description == null) {
            return false;
        }
        if (description.length() > 200) {
            log.warn("Ошибка валидации описания фильма");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        return true;
    }

    private boolean validateReleaseDate(LocalDate releaseDate) throws ValidationException {
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

    private boolean validateDuration(Integer duration) throws ValidationException {
        if (duration == null) {
            return false;
        }
        if (duration <= 0) {
            log.warn("Ошибка валидации продолжительности фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        return true;
    }

    private void validateId(Integer id) throws ValidationException {
        if (id == null) {
            log.warn("Ошибка валидации id");
            throw new ValidationException(("Поле id не может быть пустым"));
        }
        if (!films.containsKey(id)) {
            log.warn("Ошибка валидации id");
            throw new ValidationException("Фильм c id = " + id + " не найден");
        }
    }

    private Integer generateId() {
        int maxId = films.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++maxId;
    }

}
