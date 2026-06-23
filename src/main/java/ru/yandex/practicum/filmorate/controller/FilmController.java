package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getFilmStorage().findAll();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        if (film == null) {
            log.warn("Пустое тело запроса POST");
            throw new ValidationException("Тело запроса не может быть пустым");
        }
        return filmService.getFilmStorage().addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film == null) {
            log.warn("Пустое тело запроса PUT");
            throw new ValidationException("Тело запроса не может быть пустым");
        }
        return filmService.getFilmStorage().updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10") Long count) {
       return filmService.findPopularFilms(count);
    }

}
