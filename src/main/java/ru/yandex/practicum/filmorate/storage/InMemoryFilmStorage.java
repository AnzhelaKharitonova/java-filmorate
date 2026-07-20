package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    @Override
    public List<Film> findAll() {
        return films.values().stream().toList();
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return Optional.ofNullable(film);
    }

    @Override
    public Film addFilm(Film film) {
        validateName(film.getName(), true);
        validateDescription(film.getDescription());
        validateReleaseDate(film.getReleaseDate());
        validateDuration(film.getDuration());

        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм с id = {}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
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

    public Long addLike(Long id, Long userId) {
        films.get(id).getLikes().add(userId);
        log.info("Фильму с id = {} добавлен лайк от пользователя с id = {}", id, userId);
        return userId;
    }

    public Long deleteLike(Long id, Long userId) {
        films.get(id).getLikes().remove(userId);
        log.info("У фильма с id = {} удален лайк от пользователя с id = {}", id, userId);
        return userId;
    }

    public List<Film> findPopularFilms(int count) {
        return films.values().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count).toList();
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
        if (!films.containsKey(id)) {
            log.warn("Ошибка валидации id");
            throw new NotFoundException("Фильм c id = " + id + " не найден");
        }
    }

    private Long generateId() {
        long maxId = films.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++maxId;
    }

}
