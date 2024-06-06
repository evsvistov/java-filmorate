package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
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
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    // удаление друга
    public void removeFriend(Long userId, Long friendId) {
        log.info("Пользователь с id = {} удаляет из друзей пользователя с id = {}", userId, friendId);
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    // вывод списка общих друзей
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        log.info("Поиск общих друзей между пользователями с id = {} и id = {}", userId, otherUserId);
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        Set<Long> commonFriendsIds = new HashSet<>(user.getFriends());
        commonFriendsIds.retainAll(otherUser.getFriends());

        return commonFriendsIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }
}
