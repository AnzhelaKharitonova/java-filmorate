package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FilmStorageTest {
    FilmStorage filmStorage;

    @BeforeEach
    void create() {
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    void addFilm_whenNameIsNull_throwsException() {
        Film film = Film.builder()
                .name(null)
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.addFilm(film)
        );
        assertEquals("Поле название фильма должно быть заполнено", exception.getMessage());
    }

    @Test
    void addFilm_whenNameIsBlanc_throwsException() {
        Film film = Film.builder()
                .name(" ")
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.addFilm(film)
        );
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void addFilm_whenDescriptionIsVeryLong_throwsException() {
        Film film = Film.builder()
                .name("Фильм")
                .description("Из романа М. А. Булгакова «Мастер и Маргарита» взята фраза, содержащая ровно 205 \n" +
                        "символов с пробелами, которая затрагивает тему внезапной смертности человека. Эта цитата \n" +
                        "описывает непредсказуемость жизни и невозможность планировать будущее.")
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.addFilm(film)
        );
        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void addFilm_whenReleaseDate1865year_throwsException() {
        Film film = Film.builder()
                .name("Фильм")
                .releaseDate(LocalDate.of(1865, 12, 28))
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.addFilm(film)
        );
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void addFilm_whenReleaseDateInFuture_throwsException() {
        Film film = Film.builder()
                .name("Фильм")
                .releaseDate(LocalDate.now().plusYears(2))
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.addFilm(film)
        );
        assertEquals("Дата релиза фильма не может быть больше чем текущий либо следующий год",
                exception.getMessage());
    }

    @Test
    void addFilm_whenDurationIsNegative_throwsException() {
        Film film = Film.builder()
                .name("Фильм")
                .duration(-200)
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.addFilm(film)
        );
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void addFilm_whenDurationIsZero_throwsException() {
        Film film = Film.builder()
                .name("Фильм")
                .duration(0)
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.addFilm(film)
        );
        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void addFilm_whenDataIsCorrect_addsFilm() throws ValidationException, NotFoundException {
        Film film = Film.builder()
                .name("Фильм")
                .description("Очень интересный фильм")
                .releaseDate(LocalDate.of(2025, 10, 15))
                .duration(90)
                .build();

        filmStorage.addFilm(film);

        film.setId(1L);

        assertEquals(Optional.of(film), filmStorage.findFilmById(1L));
    }

    @Test
    void updateFilm_whenIdIsNull_throwsException() {
        Film film = Film.builder()
                .name("Фильм")
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.updateFilm(film)
        );
        assertEquals("Поле id не может быть пустым", exception.getMessage());
    }

    @Test
    void updateFilm_whenFilmByIdNotFound_throwsException() throws ValidationException {
        Film film = Film.builder()
                .name("Фильм")
                .build();
        filmStorage.addFilm(film);
        Film film2 = Film.builder()
                .id(2L)
                .name("Фильм")
                .build();
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmStorage.updateFilm(film2)
        );
        assertEquals("Фильм c id = 2 не найден", exception.getMessage());
    }

    @Test
    void updateFilm_whenDataIsCorrect_updatesFilm() throws ValidationException, NotFoundException {
        Film film = Film.builder()
                .name("Фильм")
                .description("Очень интересный фильм")
                .releaseDate(LocalDate.of(2025, 10, 15))
                .duration(90)
                .build();

        filmStorage.addFilm(film);

        Film newFilm = Film.builder()
                .id(1L)
                .name("Кино")
                .description("Очень интересное кино")
                .build();

        filmStorage.updateFilm(newFilm);

        Film expected = Film.builder()
                .id(1L)
                .name("Кино")
                .description("Очень интересное кино")
                .releaseDate(LocalDate.of(2025, 10, 15))
                .duration(90)
                .build();

        assertEquals(Optional.of(expected), filmStorage.findFilmById(1L));
    }

}

