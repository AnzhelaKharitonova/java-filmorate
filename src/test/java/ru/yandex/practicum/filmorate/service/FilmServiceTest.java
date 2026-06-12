package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    FilmService filmService;

    @BeforeEach
    void create() {
        filmService = new FilmService();
    }

    @Test
    void addFilm_whenNameIsNull_throwsException() {
        Film film = new Film();
        film.setName(null);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Поле название фильма должно быть заполнено", exception.getMessage());
    }

    @Test
    void addFilm_whenNameIsBlanc_throwsException() {
        Film film = new Film();
        film.setName(" ");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void addFilm_whenDescriptionIsVeryLong_throwsException() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Из романа М. А. Булгакова «Мастер и Маргарита» взята фраза, содержащая ровно 205 \n" +
                "символов с пробелами, которая затрагивает тему внезапной смертности человека. Эта цитата описывает \n" +
                "непредсказуемость жизни и невозможность планировать будущее.");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void addFilm_whenReleaseDate1865year_throwsException() {
        Film film = new Film();
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.of(1865, 12, 28));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void addFilm_whenReleaseDate2036year_throwsException() {
        Film film = new Film();
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.of(2036, 6, 12));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Дата релиза фильма не может быть больше чем текущий либо следующий год",
                exception.getMessage());
    }

    @Test
    void addFilm_whenDurationIsNegative_throwsException() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDuration(-200);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void addFilm_whenDurationIsZero_throwsException() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDuration(0);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(film)
        );
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void addFilm_whenDataIsCorrect_addsFilm() throws ValidationException {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Очень интересный фильм");
        film.setReleaseDate(LocalDate.of(2025, 10, 15));
        film.setDuration(90);

        filmService.addFilm(film);

        film.setId(1);

        assertEquals(film, filmService.getFilms().get(1));
    }

    @Test
    void updateFilm_whenIdIsNull_throwsException() {
        Film film = new Film();
        film.setName("Фильм");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmService.updateFilm(film)
        );
        assertEquals("Поле id не может быть пустым", exception.getMessage());
    }

    @Test
    void updateFilm_whenFilmByIdNotFound_throwsException() throws ValidationException {
        Film film = new Film();
        film.setName("Фильм");
        filmService.addFilm(film);
        Film film2 = new Film(2, "Фильм", null, null, null);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmService.updateFilm(film2)
        );
        assertEquals("Фильм c id = 2 не найден", exception.getMessage());
    }

    @Test
    void updateFilm_whenDataIsCorrect_updatesFilm() throws ValidationException {
        Film film = new Film(null, "Фильм", "Очень интересный фильм",
                LocalDate.of(2025, 10, 15), 90);

        filmService.addFilm(film);

        Film newFilm = new Film(1, "Кино", "Очень интересное кино", null, null);

        filmService.updateFilm(newFilm);

        Film expected = new Film(1, "Кино", "Очень интересное кино",
                LocalDate.of(2025, 10, 15), 90);

        assertEquals(expected, filmService.getFilms().get(1));
    }

}

