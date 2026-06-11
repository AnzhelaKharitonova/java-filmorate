package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    UserController userController;

    @BeforeEach
    void create() {
        userController = new UserController(new UserService());
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
        User actual = userController.addUser(new User(
                null, "Email@mail.ru", "Login", "Name", null));
        User expected = new User(1, "Email@mail.ru", "Login", "Name", null);

        assertEquals(expected, actual);
    }

    @Test
    void updateUser_whenDataIsCorrect_returnsUpdatedUser() throws ValidationException {
        User user = userController.addUser(new User(
                null, "Email@mail.ru", "Login", null, null));
        User newUser = new User(1, "Email@mail.ru", "Login", "Василий", null);
        User expected = new User(1, "Email@mail.ru", "Login", "Василий",
                null);
        User actual = userController.updateUser(newUser);

        assertEquals(expected, actual);
    }

}