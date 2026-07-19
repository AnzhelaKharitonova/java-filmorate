package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserDbStorage.class, FilmDbStorage.class, GenreStorage.class, FilmRowMapper.class, UserRowMapper.class, GenreRowMapper.class})
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreStorage genreStorage;

    User user1;
    User user2;
    User user3;
    Film film1;
    Film film2;
    Film film3;

    @Autowired
    public FilmorateApplicationTests(UserDbStorage userStorage, FilmDbStorage filmStorage, GenreStorage genreStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
    }

    @BeforeEach
    public void create() {
        user1 = User.builder()
                .name("Саша")
                .login("Sasha")
                .email("Sasha@mail.ru")
                .birthday(LocalDate.of(1988, 4, 9))
                .build();
        user2 = User.builder()
                .name("Маша")
                .login("Masha")
                .email("Masha@mail.ru")
                .birthday(LocalDate.of(1990, 5, 10))
                .build();
        user3 = User.builder()
                .name("Даша")
                .login("Dasha")
                .email("Dasha@mail.ru")
                .birthday(LocalDate.of(1983, 1, 17))
                .build();

        film1 = Film.builder()
                .name("Терминатор")
                .description("Описание")
                .duration(90)
                .releaseDate(LocalDate.of(1988, 12, 12))
                .rating(Rating.R)
                .build();
        film2 = Film.builder()
                .name("Левша")
                .description("Описание")
                .duration(90)
                .releaseDate(LocalDate.of(1988, 12, 12))
                .rating(Rating.R)
                .build();
        film3 = Film.builder()
                .name("Титаник")
                .description("Описание")
                .duration(90)
                .releaseDate(LocalDate.of(1988, 12, 12))
                .rating(Rating.R)
                .build();
    }

    @Test
    public void testAddUser() {
        User savedUser = userStorage.addUser(user1);

        Collection<User> expected = List.of(savedUser);

        assertEquals(expected, userStorage.findAll());
    }

    @Test
    public void testFindUserById() {
        User savedUser = userStorage.addUser(user1);

        Optional<User> userOptional = userStorage.findUserById(savedUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", savedUser.getId())
                );
    }

    @Test
    public void testFindAllUsers() {
        User savedUser = userStorage.addUser(user1);
        User savedUser2 = userStorage.addUser(user2);

        Collection<User> expected = List.of(savedUser, savedUser2);

        assertEquals(expected, userStorage.findAll());
    }

    @Test
    public void testUpdateUser() {
        User savedUser = userStorage.addUser(user1);
        savedUser.setName("Александр");
        userStorage.updateUser(savedUser);
        Collection<User> expected = List.of(savedUser);

        assertEquals(expected, userStorage.findAll());
    }

    @Test
    public void testAddFriend() {
        User user = userStorage.addUser(user1);
        User friend = userStorage.addUser(user2);
        userStorage.addFriend(user.getId(), friend.getId());

        assertEquals(List.of(friend), userStorage.findAllFriends(user.getId()));
    }

    @Test
    public void testDeleteFriend() {
        User user = userStorage.addUser(user1);
        User friend = userStorage.addUser(user2);
        userStorage.addFriend(user.getId(), friend.getId());
        userStorage.deleteFriend(user.getId(), friend.getId());

        assertEquals(List.of(), userStorage.findAllFriends(user.getId()));
    }

    @Test
    public void testFindAllFriend() {
        User user = userStorage.addUser(user1);
        User friend1 = userStorage.addUser(user2);
        User friend2 = userStorage.addUser(user3);
        userStorage.addFriend(user.getId(), friend1.getId());
        userStorage.addFriend(user.getId(), friend2.getId());

        assertEquals(List.of(friend1, friend2), userStorage.findAllFriends(user.getId()));
    }

    @Test
    public void testFindCommonFriend() {
        User user = userStorage.addUser(user1);
        User otherUser = userStorage.addUser(user3);
        User friend = userStorage.addUser(user2);
        userStorage.addFriend(user.getId(), friend.getId());
        userStorage.addFriend(otherUser.getId(), friend.getId());

        assertEquals(List.of(friend), userStorage.findCommonFriends(user.getId(), otherUser.getId()));
    }

    @Test
    public void testAddFilm() {
        Film savedFilm = filmStorage.addFilm(film1);

        Collection<Film> expected = List.of(savedFilm);

        assertEquals(expected, filmStorage.findAll());
    }

    @Test
    public void testFindFilmById() {
        Film savedFilm = filmStorage.addFilm(film1);

        Optional<Film> filmOptional = filmStorage.findFilmById(savedFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", savedFilm.getId())
                );
    }

    @Test
    public void testFindAllFilms() {
        Film savedFilm = filmStorage.addFilm(film1);
        Film savedFilm2 = filmStorage.addFilm(film2);

        Collection<Film> expected = List.of(savedFilm, savedFilm2);

        assertEquals(expected, filmStorage.findAll());
    }

    @Test
    public void testUpdateFilm() {
        Film savedFilm = filmStorage.addFilm(film1);
        savedFilm.setName("Терминатор2");
        filmStorage.updateFilm(savedFilm);
        Collection<Film> expected = List.of(savedFilm);

        assertEquals(expected, filmStorage.findAll());
    }

    @Test
    public void testAddLike() {
        User user = userStorage.addUser(user1);
        Film film = filmStorage.addFilm(film1);
        filmStorage.addLike(film.getId(), user.getId());

        assertEquals(Set.of(user.getId()), filmStorage.findFilmById(film.getId()).get().getLikes());
    }

    @Test
    public void testDeleteLike() {
        User user = userStorage.addUser(user1);
        Film film = filmStorage.addFilm(film1);
        filmStorage.addLike(film.getId(), user.getId());
        filmStorage.deleteLike(film.getId(), user.getId());

        assertEquals(Set.of(), filmStorage.findFilmById(film.getId()).get().getLikes());
    }

    @Test
    public void testFindPopularFilms() {
        User user = userStorage.addUser(user1);
        User otherUser = userStorage.addUser(user2);
        Film film = filmStorage.addFilm(film1);
        Film otherFilm = filmStorage.addFilm(film2);
        filmStorage.addLike(film.getId(), user.getId());
        filmStorage.addLike(film.getId(), otherUser.getId());
        filmStorage.addLike(otherFilm.getId(), otherUser.getId());

        assertEquals(List.of(film, otherFilm), filmStorage.findPopularFilms(10L));
    }

    @Test
    public void testFindAllGenres() {
        Collection<Genre> expected = List.of(
                Genre.COMEDY,
                Genre.DRAMA,
                Genre.CARTOON,
                Genre.THRILLER,
                Genre.DOCUMENTARY,
                Genre.ACTION_MOVIE);
        assertEquals(expected, genreStorage.findAll());
    }

    @Test
    public void testFindGenreById() {

        assertEquals(Optional.of(Genre.COMEDY), genreStorage.findGenreById(1L));
    }

}
