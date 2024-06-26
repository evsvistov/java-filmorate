package ru.yandex.practicum.filmorate;

import org.springframework.beans.factory.annotation.Autowired;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

@SpringBootTest
class FilmoRateApplicationTests {
    @Autowired
    private UserDbStorage userStorage;

    @Test
    public void testFindUserById() {
        // Предполагается, что пользователь с ID 1 уже существует в тестовой БД
        Optional<User> userOptional = userStorage.getUserById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }
}
