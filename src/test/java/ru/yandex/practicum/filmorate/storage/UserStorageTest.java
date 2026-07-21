package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserStorageTest {

    UserStorage userStorage;

    @BeforeEach
    void create() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void addUser_whenLoginIsNull_throwsException() {
        User user = User.builder()
                .login(null)
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Поле login должно быть заполнено", exception.getMessage());
    }

    @Test
    void addUser_whenLoginIsBlanc_throwsException() {
        User user = User.builder()
                .login(" ")
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Поле login не может быть пустым", exception.getMessage());
    }

    @Test
    void addUser_whenLoginContainsSpace_throwsException() {
        User user = User.builder()
                .login("Login login")
                .email("Email@mail.ru")
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Login не должен содержать пробелы", exception.getMessage());
    }

    @Test
    void addUser_whenEmailIsNull_throwsException() {
        User user = User.builder()
                .login("Login")
                .email(null)
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Поле email должно быть заполнено", exception.getMessage());
    }

    @Test
    void addUser_whenEmailIsBlanc_throwsException() {
        User user = User.builder()
                .login("Login")
                .email("  ")
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Поле email не может быть пустым", exception.getMessage());
    }

    @Test
    void addUser_whenEmailDoesNotContainAtSymbol_throwsException() {
        User user = User.builder()
                .login("Login")
                .email("email.mail.ru")
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("Email должен содержать символ @", exception.getMessage());
    }

    @Test
    void addUser_whenBirthdayInFuture_throwsException() {
        User user = User.builder()
                .login("Login")
                .email("email@mail.ru")
                .birthday(LocalDate.now().plusYears(1))
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.addUser(user)
        );
        assertEquals("День рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void addUser_whenDataIsCorrect_addsUser() throws ValidationException {
        User user = User.builder()
                .login("Login")
                .email("email@mail.ru")
                .birthday(LocalDate.of(1988, 4, 9))
                .name("Name")
                .build();

        userStorage.addUser(user);

        user.setId(1L);

        assertEquals(Optional.of(user), userStorage.findUserById(1L));
    }

    @Test
    void addUser_whenNameIsNull_usesLogin() throws ValidationException {
        User user = User.builder()
                .login("Login")
                .email("email@mail.ru")
                .birthday(LocalDate.of(1988, 4, 9))
                .build();

        userStorage.addUser(user);

        User expected = User.builder()
                .id(1L)
                .login("Login")
                .email("email@mail.ru")
                .birthday(LocalDate.of(1988, 4, 9))
                .name("Login")
                .build();

        assertEquals(Optional.of(expected), userStorage.findUserById(1L));
    }

    @Test
    void updateUser_whenIdIsNull_throwsException() {
        User user = User.builder()
                .login("Login")
                .email("email@mail.ru")
                .build();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.updateUser(user)
        );
        assertEquals("Поле id не может быть пустым", exception.getMessage());
    }

    @Test
    void updateUser_whenUserByIdNotFound_throwsException() throws ValidationException {
        User user = User.builder()
                .login("Login")
                .email("email@mail.ru")
                .build();
        userStorage.addUser(user);
        User user2 = User.builder()
                .id(2L)
                .login("Login")
                .email("email@mail.ru")
                .build();
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userStorage.updateUser(user2)
        );
        assertEquals("Пользователь c id = 2 не найден", exception.getMessage());
    }

    @Test
    void updateUser_whenDataIsCorrect_updatesUser() throws ValidationException {
        User user = User.builder()
                .login("Login")
                .email("email@mail.ru")
                .name("Name")
                .birthday(LocalDate.of(1988, 4, 9))
                .build();

        userStorage.addUser(user);

        User newUser = User.builder()
                .id(1L)
                .login("UserLogin")
                .email("email@mail.ru")
                .build();

        userStorage.updateUser(newUser);

        User expected = User.builder()
                .id(1L)
                .login("UserLogin")
                .email("email@mail.ru")
                .name("Name")
                .birthday(LocalDate.of(1988, 4, 9))
                .build();

        assertEquals(Optional.of(expected), userStorage.findUserById(1L));
    }

}