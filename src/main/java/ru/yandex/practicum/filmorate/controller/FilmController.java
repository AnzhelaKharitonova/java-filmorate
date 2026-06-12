package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

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
        return filmService.findAll();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        if (film == null) {
            log.warn("Пустое тело запроса POST");
            throw new ValidationException("Тело запроса не может быть пустым");
        }
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        if (film == null) {
            log.warn("Пустое тело запроса PUT");
            throw new ValidationException("Тело запроса не может быть пустым");
        }
        return filmService.updateFilm(film);
    }

}
