package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, user_name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users " +
            "SET email = ?, " +
            "login = ?, " +
            "user_name = ?, " +
            "birthday = ? " +
            "WHERE user_id = ?";
    private static final String ADD_FRIEND_QUERY =
            "INSERT INTO user_friends(user_id, friend_user_id) VALUES(?, ?)";
    private static final String DELETE_FRIEND_QUERY =
            "DELETE FROM user_friends WHERE user_id = ? AND friend_user_id = ?";
    private static final String FIND_ALL_FRIENDS_QUERY =
            "SELECT u.* " +
                    "FROM user_friends AS uf " +
                    "JOIN users AS u ON uf.friend_user_id = u.user_id " +
                    "WHERE uf.user_id = ?";
    private static final String FIND_COMMON_FRIENDS_QUERY =
            "SELECT u.* " +
                    "FROM user_friends AS uf1 " +
                    "JOIN users AS u ON uf1.friend_user_id = u.user_id " +
                    "WHERE uf1.user_id = ? AND uf1.friend_user_id IN (" +
                    "SELECT uf2.friend_user_id " +
                    "FROM user_friends AS uf2 " +
                    "WHERE uf2.user_id = ?)";


    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Collection<User> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        try {
            User result = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public User addUser(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();

        if (id == null) {
            log.warn("Ошибка в работе с БД");
            throw new InternalServerException("Не удалось сохранить данные");
        }

        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int rowsUpdated = jdbc.update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (rowsUpdated == 0) {
            log.warn("Ошибка в работе с БД");
            throw new InternalServerException("Не удалось обновить данные");
        }
        return jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, user.getId());
    }

    @Override
    public Long addFriend(Long userId, Long friendId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(ADD_FRIEND_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setLong(2, friendId);
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();

        if (id == null) {
            log.warn("Ошибка в работе с БД");
            throw new InternalServerException("Не удалось сохранить данные");
        }
        return friendId;
    }

    @Override
    public Long deleteFriend(Long id, Long friendId) {
        int rowsDeleted = jdbc.update(DELETE_FRIEND_QUERY, id, friendId);

        return friendId;
    }

    @Override
    public List<User> findAllFriends(Long id) {
        return jdbc.query(FIND_ALL_FRIENDS_QUERY, mapper, id);
    }

    @Override
    public List<User> findCommonFriends(Long id, Long otherId) {
        return jdbc.query(FIND_COMMON_FRIENDS_QUERY, mapper, id, otherId);
    }

}
