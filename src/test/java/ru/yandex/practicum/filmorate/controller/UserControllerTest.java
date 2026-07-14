package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    UserController userController;

    @BeforeEach
    void create() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void updateUser_whenUserIsNull_throwsException() {

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.updateUser(null)
        );
        assertEquals("Тело запроса не может быть пустым", exception.getMessage());
    }

    @Test
    void addUser_whenDataIsCorrect_returnsUser() throws ValidationException {
        User user = User.builder()
                .email("Email@mail.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(2026, 5, 12))
                .build();

        User expected = User.builder()
                .id(1L)
                .email("Email@mail.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(2026, 5, 12))
                .build();

        assertEquals(expected, userController.addUser(user));
    }

    @Test
    void updateUser_whenDataIsCorrect_returnsUpdatedUser() throws ValidationException {
        User user = User.builder()
                .email("Email@mail.ru")
                .login("Login")
                .build();

        userController.addUser(user);
        User newUser = User.builder()
                .id(1L)
                .email("Email@mail.ru")
                .login("Login")
                .name("Василий")
                .birthday(LocalDate.of(1988, 4, 9))
                .build();

        User expected = User.builder()
                .id(1L)
                .email("Email@mail.ru")
                .login("Login")
                .name("Василий")
                .birthday(LocalDate.of(1988, 4, 9))
                .build();

        User actual = userController.updateUser(newUser);

        assertEquals(expected, actual);
    }

}