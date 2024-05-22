package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    // получение списка всех пользователей
    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    // создание пользователя
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(getNextId());
        log.info("Создание нового пользователя: {}", user);
        checkAndSetUserName(user);
        users.put(user.getId(), user);
        return user;
    }

    // обновление пользователя
    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        if (!users.containsKey(newUser.getId())) {
            log.error("Ошибка: пользователь с id = {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }
        log.info("Обновление пользователя: {}", newUser);
        checkAndSetUserName(newUser);
        User existingUser = users.get(newUser.getId());
        users.put(existingUser.getId(), newUser);
        return newUser;
    }

    private void checkAndSetUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("Имя NULL, в качестве имени используется login, user_id = {}", user.getId());
            user.setName(user.getLogin());
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
