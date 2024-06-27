package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final Map<Long, Set<Long>> likes = new HashMap<>();

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // получение всех фильмов
    public Collection<Film> findAll() {
        log.debug("Получение всех фильмов");
        return filmStorage.findAll();
    }

    // добавление фильма
    public Film create(Film film) {
        log.debug("Создание фильма");
        Film createdFilm = filmStorage.create(film);
        return createdFilm;
    }

    // обновление фильма
    public Film update(Film newFilm) {
        log.info("Обновление фильма: {}", newFilm);
        return filmStorage.update(newFilm);
    }

    // удаление фильма
    public boolean delete(Long filmId) {
        log.info("Удаление фильма с id: {}", filmId);
        boolean result = filmStorage.delete(filmId);
        if (!result) {
            log.warn("Не удалось удалить фильм с id: {}", filmId);
        }
        return result;
    }

    // поиск фильма по id
    public Film findById(long id) {
        log.info("Поиск фильма с id: {}", id);
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    // добавление лайка
    public boolean addLike(Long filmId, Long userId) {
        log.info("Добавление лайка от пользователя {} фильму {}", userId, filmId);
        filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        return likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    // удаление лайка
    public boolean removeLike(Long filmId, Long userId) {
        log.info("Удаление лайка от пользователя {} фильму {}", userId, filmId);
        filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        if (likes.containsKey(filmId)) {
            boolean removed = likes.get(filmId).remove(userId);
            if (!removed) {
                log.warn("Попытка удалить лайк от пользователя {} фильму {}, но лайк не найден", userId, filmId);
            }
            return removed;
        } else {
            log.warn("Попытка удалить лайк от пользователя {} фильму {}, но лайк не найден", userId, filmId);
            return false;
        }
    }

    // топ фильмов
    public List<Film> getTopFilms(int count) {
        log.info("Получение топ {} фильмов", count);
        return likes.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .limit(count)
                .map(entry -> filmStorage.getFilmById(entry.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
