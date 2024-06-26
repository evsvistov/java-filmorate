package ru.yandex.practicum.filmorate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.MethodSource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

@SpringBootTest
public class UserValidationTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static Stream<User> validUserProvider() {
        return Stream.of(
                new User(null, "valid@example.com", "validLogin", "Valid Name", LocalDate.of(1999, 12, 31), new HashSet<>())
        );
    }

    static Stream<User> invalidUserProvider() {
        return Stream.of(
                new User(null, "invalid-email", "validLogin", "Valid Name", LocalDate.of(1999, 12, 31), new HashSet<>()),
                new User(null, "valid@example.com", "", "Valid Name", LocalDate.of(1999, 12, 31), new HashSet<>()),
                new User(null, "valid@example.com", "validLogin", "Valid Name", LocalDate.now().plusDays(1), new HashSet<>())
        );
    }

    @ParameterizedTest
    @MethodSource("validUserProvider")
    void testValidUser(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid user");
    }

    @ParameterizedTest
    @MethodSource("invalidUserProvider")
    void testInvalidUsers(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(!violations.isEmpty(), "There should be violations for an invalid user");
    }
}
