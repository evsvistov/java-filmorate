package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    private final GenreService genreService;

    @Autowired
    public FilmController(FilmService filmService, GenreService genreService) {
        this.filmService = filmService;
        this.genreService = genreService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Поиск фильма по id: {}", id);
        return filmService.findById(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Film film) {
        try {
            Film createdFilm = filmService.create(film);
            return new ResponseEntity<>(createdFilm, HttpStatus.CREATED);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
        } catch (MpaNotFoundException | GenreNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal Server Error"));
        }
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма: {}", film);
        try {
            Film updatedFilm = filmService.update(film);
            return ResponseEntity.ok(updatedFilm);
        } catch (ResourceNotFoundException ex) {
            log.error("Фильм не найден: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Ошибка при обновлении фильма: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal Server Error"));
        }
    }

    // удаление фильам
    @DeleteMapping
    public boolean delete(@Valid @RequestBody Long filmId) {
        log.info("Удаление фильма с id: {}", filmId);
        return filmService.delete(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавление лайка к фильму с id {} пользователем {}: ", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удаление лайка фильма с id {} пользователем {}: ", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Вывод топ {} фильмов: ", count);
        return filmService.getTopFilms(count);
    }

    @PostMapping("/{filmId}/genres/{genreId}")
    public void addGenreToFilm(@PathVariable Long filmId, @PathVariable Long genreId) {
        log.info("Добавление фильму {} жанра {}: ", filmId, genreId);
        genreService.addGenreToFilm(filmId, genreId);
    }

    @DeleteMapping("/{filmId}/genres/{genreId}")
    public void removeGenreFromFilm(@PathVariable Long filmId, @PathVariable Long genreId) {
        log.info("Удаление жанра {} из фильма {}: ", filmId, genreId);
        genreService.removeGenreFromFilm(filmId, genreId);
    }

}
