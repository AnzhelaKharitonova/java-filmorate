package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (user == null) {
            log.warn("Пустое тело запроса POST");
            throw new ValidationException("Тело запроса не может быть пустым");
        }
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user == null) {
            log.warn("Пустое тело запроса PUT");
            throw new ValidationException("Тело запроса не может быть пустым");
        }
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Long addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Long deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable Long id) {
        return userService.findAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.findCommonFriends(id, otherId);
    }

}
