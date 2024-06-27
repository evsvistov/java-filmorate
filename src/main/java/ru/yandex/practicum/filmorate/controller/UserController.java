package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

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
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("Поиск пользователя по id: {}", id);
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    // создание пользователя
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        log.info("Добавление пользователя : {}", user);
        User createdUser = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // обновление пользователя
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody User user) {
        log.info("Обновление пользователя : {}", user);
        try {
            User updatedUser = userService.update(user);
            return ResponseEntity.ok(updatedUser);
        } catch (NotFoundException ex) {
            log.error("Ошибка обновления: {}", ex.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Not Found");
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // удаление пользователя
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        log.info("Удаление пользователя с id: {}", userId);
        userService.delete(userId);
        return ResponseEntity.ok().build();
    }

    // добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с id = {} добавляет в друзья пользователя с id = {}", id, friendId);
        userService.addFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    // удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с id = {} удаляет из друзей пользователя с id = {}", id, friendId);
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    // получение списка друзей
    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<User>> getFriends(@PathVariable Long id) {
        log.info("Получение списка друзей у пользваотеля с id: {}", id);
        Set<User> friends = userService.getFriends(id);
        return ResponseEntity.ok(friends);
    }

    // поиск общих друзей
    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Поиск общих друзей между пользователями с id = {} и id = {}", id, otherId);
        List<User> commonFriends = new ArrayList<>(userService.getCommonFriends(id, otherId));
        return ResponseEntity.ok(commonFriends);
    }

}
