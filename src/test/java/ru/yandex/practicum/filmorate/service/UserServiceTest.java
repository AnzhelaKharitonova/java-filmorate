package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceTest {
    UserService userService;
    User user1;
    User user2;
    User user3;

    @BeforeEach
    void create() {
        UserStorage us = new InMemoryUserStorage();
        userService = new UserService(us);
        user1 = User.builder()
                .name("Саша")
                .login("Sasha")
                .email("Sasha@mail.ru")
                .build();
        user2 = User.builder()
                .name("Маша")
                .login("Masha")
                .email("Masha@mail.ru")
                .build();
        user3 = User.builder()
                .name("Даша")
                .login("Dasha")
                .email("Dasha@mail.ru")
                .build();

        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
    }

    @Test
    void findAllFriends_returnsFriends() {
        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 3L);
        List<User> expected = List.of(user2, user3);
        assertEquals(expected, userService.findAllFriends(1L));
    }

    @Test
    void findCommonFriends_returnsCommonFriends() {
        userService.addFriend(1L, 2L);
        userService.addFriend(1L, 2L);
        userService.addFriend(3L, 2L);

        List<User> expected = List.of(user2);
        assertEquals(expected, userService.findCommonFriends(1L, 3L));
    }

}