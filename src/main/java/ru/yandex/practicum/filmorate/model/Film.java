package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private long duration;

    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    public boolean isReleaseDateValid() {
        if (releaseDate == null) {
            return true;
        }
        LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);
        return !releaseDate.isBefore(earliestReleaseDate);
    }

    @JsonProperty("mpa")
    private MpaRating mpaRatingId;

    @JsonProperty("genres")
    private Set<Genre> genreIds;

    public Film(Long id, String name, String description, LocalDate releaseDate, int duration, MpaRating mpaRatingId,
                Set<Genre> genreIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpaRatingId = mpaRatingId;
        this.genreIds = genreIds;
    }
}
