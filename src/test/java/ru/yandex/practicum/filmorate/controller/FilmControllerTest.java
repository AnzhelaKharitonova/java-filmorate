package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    FilmController filmController;

    @BeforeEach
    void create() {
        filmController = new FilmController(new FilmService(new InMemoryUserStorage(), new InMemoryFilmStorage()));
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
        Film film = Film.builder()
                .name("Фильм")
                .build();

        Film expected = Film.builder()
                .id(1L)
                .name("Фильм")
                .build();

        assertEquals(expected, filmController.addFilm(film));
    }

    @Test
    void updateFilm_whenDataIsCorrect_returnsUpdatedFilm() throws ValidationException {
        Film film = Film.builder()
                        .name("Фильм")
                                .description("Описание")
                                        .duration(90)
                                                .build();

                filmController.addFilm(film);
        Film newFilm = Film.builder()
                .id(1L)
                .name("Фильм")
                .description("Обновленное описание")
                .build();

        Film expected = Film.builder()
                .id(1L)
                .name("Фильм")
                .description("Обновленное описание")
                .duration(90)
                .build();

        Film actual = filmController.updateFilm(newFilm);

        assertEquals(expected, actual);
    }

}

