package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    UserService userService;

    @BeforeEach
    void create() {
        userService = new UserService();
    }

    @Test
    void addUser_whenLoginIsNull_throwsException() {
        User user = new User();
        user.setLogin(null);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.addUser(user)
        );
        assertEquals("Поле login должно быть заполнено", exception.getMessage());
    }

    @Test
    void addUser_whenLoginIsBlanc_throwsException() {
        User user = new User();
        user.setLogin(" ");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.addUser(user)
        );
        assertEquals("Поле login не может быть пустым", exception.getMessage());
    }

    @Test
    void addUser_whenLoginContainsSpace_throwsException() {
        User user = new User();
        user.setLogin("Login login");
        user.setEmail("Email@mail.ru");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.addUser(user)
        );
        assertEquals("Login не должен содержать пробелы", exception.getMessage());
    }

    @Test
    void addUser_whenEmailIsNull_throwsException() {
        User user = new User();
        user.setLogin("Login");
        user.setEmail(null);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.addUser(user)
        );
        assertEquals("Поле email должно быть заполнено", exception.getMessage());
    }

    @Test
    void addUser_whenEmailIsBlanc_throwsException() {
        User user = new User();
        user.setLogin("Login");
        user.setEmail("  ");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.addUser(user)
        );
        assertEquals("Поле email не может быть пустым", exception.getMessage());
    }

    @Test
    void addUser_whenEmailDoesNotContainAtSymbol_throwsException() {
        User user = new User();
        user.setLogin("Login");
        user.setEmail("email.mail.ru");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.addUser(user)
        );
        assertEquals("Email должен содержать символ @", exception.getMessage());
    }

    @Test
    void addUser_whenBirthdayInFuture_throwsException() {
        User user = new User();
        user.setLogin("Login");
        user.setEmail("email@mail.ru");
        user.setBirthday(LocalDate.now().plusYears(1));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.addUser(user)
        );
        assertEquals("День рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void addUser_whenDataIsCorrect_addsUser() throws ValidationException {
        User user = new User();
        user.setLogin("Login");
        user.setEmail("Email@mail.ru");
        user.setBirthday(LocalDate.of(1988, 4, 9));
        user.setName("Name");

        userService.addUser(user);

        user.setId(1);

        assertEquals(user, userService.getUsers().get(1));
    }

    @Test
    void addUser_whenNameIsNull_usesLogin() throws ValidationException {
        User user = new User();
        user.setLogin("Login");
        user.setEmail("Email@mail.ru");
        user.setBirthday(LocalDate.of(1988, 4, 9));

        userService.addUser(user);

        User expected = new User(1, "Email@mail.ru", "Login", "Login",
                LocalDate.of(1988, 4, 9));

        assertEquals(expected, userService.getUsers().get(1));
    }

    @Test
    void updateUser_whenIdIsNull_throwsException() {
        User user = new User();
        user.setLogin("Login");
        user.setEmail("Email@mail.ru");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.updateUser(user)
        );
        assertEquals("Поле id не может быть пустым", exception.getMessage());
    }

    @Test
    void updateUser_whenUserByIdNotFound_throwsException() throws ValidationException {
        User user = new User();
        user.setLogin("Login");
        user.setEmail("Email@mail.ru");
        userService.addUser(user);
        User user2 = new User(2, "Email@mail.ru", "Login", null, null);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.updateUser(user2)
        );
        assertEquals("Пользователь c id = 2 не найден", exception.getMessage());
    }

    @Test
    void updateUser_whenDataIsCorrect_updatesUser() throws ValidationException {
        User user = new User(null, "Email@mail.ru", "Login", "Name",
                LocalDate.of(1988, 4, 9));

        userService.addUser(user);

        User newUser = new User(1, "Email@mail.ru", "UserLogin", null, null);

        userService.updateUser(newUser);

        User expected = new User(1, "Email@mail.ru", "UserLogin", "Name",
                LocalDate.of(1988, 4, 9));

        assertEquals(expected, userService.getUsers().get(1));
    }

}