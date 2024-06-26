package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class FilmValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static Stream<Film> validFilmProvider() {
        return Stream.of(
                new Film(null, "Valid Film", "This is a valid description.",
                        LocalDate.of(1995, 12, 28), 120,
                        new MpaRating(1L,"G"), new HashSet<>())
        );
    }

    static Stream<Object[]> invalidFilmProvider() {
        return Stream.of(
                new Object[] {
                        new Film(null, "", "This is a valid description.",
                                LocalDate.of(1995, 12, 28), 120,
                                new MpaRating(1L,"G"), new HashSet<>()),
                        "Название не может быть пустым"
                },
                new Object[] {
                        new Film(null, "Valid Film", "This description is way too long.".repeat(10),
                                LocalDate.of(1995, 12, 28), 120,
                                new MpaRating(1L,"G"), new HashSet<>()),
                        "Максимальная длина описания — 200 символов"
                },
                new Object[] {
                        new Film(null, "Valid Film", "This is a valid description.",
                                LocalDate.now().plusDays(1), 120,
                                new MpaRating(1L,"G"), new HashSet<>()),
                        "Дата релиза не может быть в будущем"
                },
                new Object[] {
                        new Film(null, "Valid Film", "This is a valid description.",
                                LocalDate.of(1995, 12, 28), -100,
                                new MpaRating(1L,"G"), new HashSet<>()),
                        "Продолжительность фильма должна быть положительным числом"
                }
        );
    }

    @ParameterizedTest
    @MethodSource("validFilmProvider")
    void testValidFilm(Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Valid film should have no violations");
    }

    @ParameterizedTest
    @MethodSource("invalidFilmProvider")
    void testInvalidFilms(Film film, String expectedMessage) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }
}
