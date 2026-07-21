package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface UserStorage {

    Collection<User> findAll();

    Optional<User> findUserById(Long id);

    User addUser(User user);

    User updateUser(User user);

    Long addFriend(Long userId, Long friendId);

    Long deleteFriend(Long id, Long friendId);

    List<User> findAllFriends(Long id);

    List<User> findCommonFriends(Long id, Long otherId);
}
