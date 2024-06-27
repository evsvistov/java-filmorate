package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO USERS (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().longValue());
            return user;
        } else {
            throw new InternalServerException("Не удалось сохранить данные пользователя");
        }
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("У пользователя не задан id=" + user.getId());
        }
        if (!existsById(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден.");
        }
        jdbcTemplate.update("UPDATE USERS SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
                user.getEmail(), user.getLogin(), user.getName(), java.sql.Date.valueOf(user.getBirthday()), user.getId());
        return user;

    }

    @Override
    public boolean delete(Long userId) {
        if (!existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        String sql = "DELETE FROM USERS WHERE id = ?";
        return jdbcTemplate.update(sql, userId) > 0;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        User user = jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
        if (user != null) {
            user.setFriendIds(getFriends(id));
        }
        return Optional.ofNullable(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (!existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        if (!existsById(friendId)) {
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден.");
        }
        String sql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        if (!existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        if (!existsById(friendId)) {
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден.");
        }
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Set<Long> getFriends(Long userId) {
        if (!existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        String sql = "SELECT friend_id FROM friendship WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, userId));
    }

    @Override
    public Set<Long> getCommonFriends(Long userId, Long otherUserId) {
        String sql = """
                SELECT f1.friend_id
                FROM friendship f1
                JOIN friendship f2 ON f1.friend_id = f2.friend_id
                WHERE f1.user_id = ? AND f2.user_id = ?
                """;
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, userId, otherUserId));
    }

    private boolean existsById(Long id) {
        String sql = "SELECT EXISTS (SELECT 1 FROM users WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate(),
                null
        );
    }
}
