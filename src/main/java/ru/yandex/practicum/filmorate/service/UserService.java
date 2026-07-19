package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User addUser(User user) {
        validateEmail(user.getEmail(), true);
        validateLogin(user.getLogin(), true);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        validateBirthday(user.getBirthday());
        userStorage.addUser(user);
        log.info("Добавлен новый пользователь с id = {}", user.getId()); //todo проверить id
        return user;
    }

    public User findUserById(Long id) {
        return userStorage.findUserById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    public User updateUser(User user) {
        validateId(user.getId());
        User updatedUser = findUserById(user.getId());
        if (validateEmail(user.getEmail(), false)) {
            updatedUser.setEmail(user.getEmail());
        }
        if (validateLogin(user.getLogin(), false)) {
            updatedUser.setLogin(user.getLogin());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (validateBirthday(user.getBirthday())) {
            updatedUser.setBirthday(user.getBirthday());
        }
        userStorage.updateUser(updatedUser);
        log.info("Обновлены данные пользователя с id = {}", user.getId());
        return updatedUser;
    }

    public Long addFriend(Long id, Long friendId) {
        findUserById(id);
        findUserById(friendId);
        userStorage.addFriend(id, friendId);
        log.info("Пользователю с id = {} добавлен друг с id = {}", id, friendId);
        return friendId;
    }

    public Long deleteFriend(Long id, Long friendId) {
        findUserById(id);
        findUserById(friendId);
        userStorage.deleteFriend(id, friendId);
        log.info("У пользователя с id = {} удален из друзей пользователь с id = {} ", id, friendId);
        return friendId;
    }

    public List<User> findAllFriends(Long id) {
        findUserById(id);
        return userStorage.findAllFriends(id);
    }

    public List<User> findCommonFriends(Long id, Long otherId) {
        findUserById(id);
        findUserById(otherId);
        return userStorage.findCommonFriends(id, otherId);
    }

    private boolean validateLogin(String login, boolean isNewUser) {
        if (login == null) {
            if (isNewUser) {
                log.warn("Ошибка валидации логина");
                throw new ValidationException("Поле login должно быть заполнено");
            }
            return false;
        }
        if (login.isBlank()) {
            log.warn("Ошибка валидации логина");
            throw new ValidationException("Поле login не может быть пустым");
        }
        if (login.contains(" ")) {
            log.warn("Ошибка валидации логина");
            throw new ValidationException("Login не должен содержать пробелы");
        }
        return true;
    }

    private boolean validateEmail(String email, boolean isNewUser) {
        if (email == null) {
            if (isNewUser) {
                log.warn("Ошибка валидации email");
                throw new ValidationException("Поле email должно быть заполнено");
            }
            return false;
        }
        if (email.isBlank()) {
            log.warn("Ошибка валидации email");
            throw new ValidationException("Поле email не может быть пустым");
        }
        if (!email.contains("@")) {
            log.warn("Ошибка валидации email");
            throw new ValidationException("Email должен содержать символ @");
        }
        return true;
    }

    private boolean validateBirthday(LocalDate birthday) {
        if (birthday == null) {
            return false;
        }
        if (birthday.isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации дня рождения");
            throw new ValidationException("День рождения не может быть в будущем");
        }
        return true;
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new ValidationException(("Поле id не может быть пустым"));
        }
    }

}
