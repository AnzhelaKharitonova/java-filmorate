package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    FilmController filmController;

    @BeforeEach
    void create() {
        filmController = new FilmController(new FilmService());

    }

    @Test
    void updateFilm_whenFilmIsNull_throwsException() {

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.updateFilm(null)
        );
        assertEquals("Тело запроса не может быть пустым", exception.getMessage());
    }

    @Test
    void addFilm_whenDataIsCorrect_returnsFilm() throws ValidationException {
        Film actual = filmController.addFilm(new Film(
                null, "Фильм", null, null, null));
        Film expected = new Film(1, "Фильм", null, null, null);

        assertEquals(expected, actual);
    }

    @Test
    void updateFilm_whenDataIsCorrect_returnsUpdatedFilm() throws ValidationException {
        Film film = filmController.addFilm(new Film(
                null, "Фильм", "Описание", null, 90));
        Film newFilm = new Film(1, "Фильм", "Обновленное описание", null, null);
        Film expected = new Film(1, "Фильм", "Обновленное описание", null,
                90);
        Film actual = filmController.updateFilm(newFilm);

        assertEquals(expected, actual);
    }

}

