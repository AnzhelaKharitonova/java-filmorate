package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Getter
public class UserService {

    Map<Integer, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User addUser(User user) throws ValidationException {
        validateLogin(user.getLogin(), true);
        validateEmail(user.getEmail(), true);

        user.setId(generateId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с id = {}", user.getId());
        return user;
    }

    public User updateUser(User user) throws ValidationException {
        validateId(user.getId());

        User updatableUser = users.get(user.getId());
        if (validateLogin(user.getLogin(), false)) {
            updatableUser.setLogin(user.getLogin());
        }
        if (validateEmail(user.getEmail(), false)) {
            updatableUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updatableUser.setName(user.getName());
        }
        if (user.getBirthday() != null) {
            updatableUser.setBirthday(user.getBirthday());
        }
        users.put(updatableUser.getId(), updatableUser);
        log.info("Обновлен пользователь с id = {}", user.getId());
        return updatableUser;
    }

    private boolean validateLogin(String login, boolean isNewUser) throws ValidationException {
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

    private boolean validateEmail(String email, boolean isNewUser) throws ValidationException {
        if (email == null) {
            if (isNewUser) {
                log.warn("Ошибка валидации email");
                throw new ValidationException("Поле email не может быть пустым");
            }
            return false;
        }
        return true;
    }

    private void validateId(Integer id) throws ValidationException {
        if (id == null) {
            throw new ValidationException(("Поле id не может быть пустым"));
        }
        if (!users.containsKey(id)) {
            throw new ValidationException("Пользователь c id = " + id + " не найден");
        }
    }

    private Integer generateId() {
        int maxId = users.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++maxId;
    }

}
