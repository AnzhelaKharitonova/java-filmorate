/*
package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest(properties = {
        "spring.sql.init.mode=always\n" +
                "spring.datasource.url=jdbc:h2:file:./db/filmorate;MODE=PostgreSQL\n" +
                "spring.datasource.driverClassName=org.h2.Driver"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserDbStorage.class, FilmDbStorage.class, FilmRowMapper.class, UserRowMapper.class, GenreRowMapper.class})

class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    User user1;
    User user2;
    User user3;
    Film film1;
    Film film2;
    Film film3;

    @Autowired
    public FilmorateApplicationTests(UserDbStorage userStorage, FilmDbStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @BeforeEach
    public void create() {
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
    }

    @Test
    public void testAddUser() {
        userStorage.addUser(user1);

        user1.setId(1L);
        Collection<User> expected = List.of(user1);

        assertEquals(expected, userStorage.findAll());

    }

    @Test
    public void testFindUserById() {
        userStorage.addUser(user1);

        Optional<User> userOptional = userStorage.findUserById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

}
*/
