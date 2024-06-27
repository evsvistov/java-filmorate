package ru.yandex.practicum.filmorate;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class FilmoRateApplicationTests {
    @Autowired
    private UserDbStorage userStorage;

    @Test
    void testFindUserById() {
        // Предполагаем, что данные пользователя с ID 1 уже добавлены в тестовую БД
        Optional<User> userOptional = userStorage.getUserById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getId()).isEqualTo(1L));
    }
}
