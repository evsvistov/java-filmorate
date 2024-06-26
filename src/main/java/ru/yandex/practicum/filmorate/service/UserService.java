package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // получение списка всех пользователей
    public Collection<User> findAll() {
        log.debug("Получение всех пользователей");
        return userStorage.findAll();
    }

    // создание пользователя
    public User addUser(User user) {
        log.info("Создание нового пользователя: {}", user);
        return userStorage.create(user);
    }

    // обновление пользователя
    public User update(User user) {
        log.info("Обновление пользователя: {}", user);
        return userStorage.update(user);
    }

    // удаление пользователя
    public boolean delete(Long userId) {
        log.info("Удаление пользователя с id: {}", userId);
        return userStorage.delete(userId);
    }

    // получение пользователя по id
    public User getUserById(Long id) {
        log.info("Поиск пользователя с id: {}", id);
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    // добавление друга
    public void addFriend(Long userId, Long friendId) {
        log.info("Пользователь с id = {} добавляет в друзья пользователя с id = {}", userId, friendId);
        userStorage.addFriend(userId, friendId);
    }

    // удаление друга
    public void removeFriend(Long userId, Long friendId) {
        log.info("Пользователь с id = {} удаляет из друзей пользователя с id = {}", userId, friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public Set<User> getFriends(Long userId) {
        Set<Long> friendIds = userStorage.getFriends(userId);
        return friendIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(Long userId, Long otherUserId) {
        Set<Long> commonFriendIds = userStorage.getCommonFriends(userId, otherUserId);
        return commonFriendIds.stream()
                .map(this::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

}
