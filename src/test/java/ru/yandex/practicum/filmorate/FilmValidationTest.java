package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilmValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("This is a valid description.");
        film.setReleaseDate(LocalDate.of(2024, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("This is a valid description.");
        film.setReleaseDate(LocalDate.of(2024, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void testLongDescription() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("This description is way too long.".repeat(10));
        film.setReleaseDate(LocalDate.of(2024, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Максимальная длина описания — 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void testFutureReleaseDate() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("This is a valid description.");
        film.setReleaseDate(LocalDate.now().plusDays(1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Дата релиза не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void testNegativeDuration() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("This is a valid description.");
        film.setReleaseDate(LocalDate.of(2024, 1, 1));
        film.setDuration(-100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Продолжительность фильма должна быть положительным числом", violations.iterator().next().getMessage());
    }

}
