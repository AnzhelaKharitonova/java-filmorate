package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmServiceTest {
    FilmService filmService;
    User user1;
    User user2;
    User user3;
    Film film1;
    Film film2;
    Film film3;

    @BeforeEach
    void create() {
        UserStorage us = new InMemoryUserStorage();
        FilmStorage fs = new InMemoryFilmStorage();
        filmService = new FilmService(us, fs);
        user1 = User.builder()
                .name("Саша")
                .login("Sasha")
                .email("Sasha@mail.ru")
                .build();
        user2 = User.builder()
                .name("Маша")
                .login("Masha")
                .email("Masha@mail.ru")
                .build();
        user3 = User.builder()
                .name("Даша")
                .login("Dasha")
                .email("Dasha@mail.ru")
                .build();

        film1 = Film.builder()
                .name("Терминатор")
                .build();
        film2 = Film.builder()
                .name("Левша")
                .build();
        film3 = Film.builder()
                .name("Титаник")
                .build();
        filmService.getFilmStorage().addFilm(film1);
        filmService.getFilmStorage().addFilm(film2);
        filmService.getFilmStorage().addFilm(film3);

        filmService.getUserStorage().addUser(user1);
        filmService.getUserStorage().addUser(user2);
        filmService.getUserStorage().addUser(user3);
    }

    @Test
    void addLike_whenDataIsCorrect_addsLike() {
        filmService.addLike(1L, 1L);
        Set<Long> expected = Set.of(1L);
        assertEquals(expected, filmService.getFilmStorage().findFilmById(1L).getLikes());
    }

    @Test
    void deleteLike_whenDataIsCorrect_deletesLike() {
        filmService.addLike(1L, 1L);
        filmService.addLike(1L, 2L);
        filmService.deleteLike(1L, 2L);
        Set<Long> expected = Set.of(1L);
        assertEquals(expected, filmService.getFilmStorage().findFilmById(1L).getLikes());
    }

    @Test
    void findPopularFilms() {
        filmService.addLike(1L, 1L);
        filmService.addLike(1L, 2L);
        filmService.addLike(1L, 3L);
        filmService.addLike(2L, 1L);
        filmService.addLike(2L, 2L);
        filmService.addLike(3L, 1L);

        List<Film> expected = List.of(film1, film2, film3);
        assertEquals(expected, filmService.findPopularFilms(5L));
    }

}