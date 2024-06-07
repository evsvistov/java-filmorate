package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // получение списка всех пользователей
    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Поиск пользователя по id: {}", id);
        return userService.getUserById(id);
    }

    // создание пользователя
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Добавление пользователя : {}", user);
        return userService.addUser(user);
    }

    // обновление пользователя
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Обновление пользователя : {}", user);
        return userService.update(user);
    }

    // удаление пользователя
    @DeleteMapping
    public boolean delete(@Valid @RequestBody Long userId) {
        log.info("Удаление пользователя с id: {}", userId);
        return userService.delete(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с id = {} добавляет в друзья пользователя с id = {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с id = {} удаляет из друзей пользователя с id = {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получение списка друзей у пользваотеля с id: {}", id);
        User user = userService.getUserById(id);
        return user.getFriends().stream()
                .map(userService::getUserById)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Поиск общих друзей между пользователями с id = {} и id = {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

}
