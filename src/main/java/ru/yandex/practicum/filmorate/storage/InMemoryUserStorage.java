package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user;
    }

    @Override
    public User addUser(User user) {
        validateLogin(user.getLogin(), true);
        validateEmail(user.getEmail(), true);
        validateBirthday(user.getBirthday());

        user.setId(generateId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с id = {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
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
        if (validateBirthday(user.getBirthday())) {
            updatableUser.setBirthday(user.getBirthday());
        }
        users.put(updatableUser.getId(), updatableUser);
        log.info("Обновлен пользователь с id = {}", user.getId());
        return updatableUser;
    }

    @Override
    public boolean validateLogin(String login, boolean isNewUser) {
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

    @Override
    public boolean validateEmail(String email, boolean isNewUser) {
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

    @Override
    public boolean validateBirthday(LocalDate birthday) {
        if (birthday == null) {
            return false;
        }
        if (birthday.isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации дня рождения");
            throw new ValidationException("День рождения не может быть в будущем");
        }
        return true;
    }

    @Override
    public void validateId(Long id) {
        if (id == null) {
            throw new ValidationException(("Поле id не может быть пустым"));
        }
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь c id = " + id + " не найден");
        }
    }

    @Override
    public Long generateId() {
        long maxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++maxId;
    }

}
