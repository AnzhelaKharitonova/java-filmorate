package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@Getter
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Long id, Long friendId) {
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        log.info("Пользователи с id = {} и id = {} добавлены друг другу в друзья", id, friendId);
        return friend;
    }

    public User deleteFriend(Long id, Long friendId) {
        User user = userStorage.findUserById(id);
        User friend = userStorage.findUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        log.info("Пользователи с id = {} и id = {} удалены из друзей друг у друга", id, friendId);
        return friend;
    }

    public List<User> findAllFriends(Long id) {
        User user = userStorage.findUserById(id);
        return user.getFriends().stream()
                .map(friendId -> userStorage.findUserById(friendId))
                .toList();
    }

    public List<User> findCommonFriends(Long id, Long otherId) {
        User user = userStorage.findUserById(id);
        User otherUser = userStorage.findUserById(otherId);
        return user.getFriends().stream()
                .filter(friendId -> otherUser.getFriends().contains(friendId))
                .map(friendId -> userStorage.findUserById(friendId))
                .toList();
    }

}
