package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@Getter
public class FilmService {
    private UserStorage userStorage;
    private FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void addLike(Long id, Long userId) {
        Film film = filmStorage.findFilmById(id);
        userStorage.findUserById(userId);
        film.getLikes().add(userId);
        log.info("Фильму с id = {} добавлен лайк от пользователя с id = {}", id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        Film film = filmStorage.findFilmById(id);
        userStorage.findUserById(userId);
        film.getLikes().remove(userId);
        log.info("У фильма с id = {} удален лайк от пользователя с id = {}", id, userId);
    }

    public List<Film> findPopularFilms(Long count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count).toList();
    }

}
