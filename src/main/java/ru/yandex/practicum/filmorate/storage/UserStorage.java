package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;


public interface UserStorage {

    Collection<User> findAll();

    User findUserById(Long id);

    User addUser(User user);

    User updateUser(User user);

    boolean validateLogin(String login, boolean isNewUser);

    boolean validateEmail(String email, boolean isNewUser);

    boolean validateBirthday(LocalDate birthday);

    void validateId(Long id);

    Long generateId();
}
