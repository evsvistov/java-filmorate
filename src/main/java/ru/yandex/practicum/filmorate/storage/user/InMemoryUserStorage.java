package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    // получение списка всех пользователей
    @Override
    public Collection<User> findAll() {
        log.debug("Вывод пользователей из хранилища");
        return users.values();
    }

    // создание пользователя
    @Override
    public User create(User user) {
        user.setId(getNextId());
        log.info("Создание нового пользователя в хранилище: {}", user);
        checkAndSetUserName(user);
        users.put(user.getId(), user);
        return user;
    }

    // обновление пользователя
    @Override
    public User update(User newUser) {
        if (checkContainsUser(newUser)) {
            log.info("Обновление пользователя в хранилище: {}", newUser);
            checkAndSetUserName(newUser);
            User existingUser = users.get(newUser.getId());
            users.put(existingUser.getId(), newUser);
        }
        return newUser;
    }

    // удаление пользователя
    @Override
    public boolean delete(Long userId) {
        log.info("Удаление пользователя с id из хранилища: {}", userId);
        return users.remove(userId) != null;
    }

    // получение пользователя по id
    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private boolean checkContainsUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Ошибка: пользователь с id = {} в хранилище не найден", user.getId());
            throw new NotFoundException("Пользователь с id = " + user.getId() + "в хранилище не найден");
        }
        return true;
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
