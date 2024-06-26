package ru.yandex.practicum.filmorate.dao.impl.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    //методы добавления, удаления и модификации объектов
    private final Map<Long, Film> films = new HashMap<>();

    // получение всех фильмов
    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    // добавление фильма
    @Override
    public Film create(Film film) {
        log.info("Добавление нового фильма в хранилище: {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    // обновление фильма
    @Override
    public Film update(Film newFilm) {
        if (checkContainsFilm(newFilm)) {
            log.info("Обновление фильма в хранилище с ID {}: {}", newFilm.getId(), newFilm);
            Film existingFilm = films.get(newFilm.getId());
            films.put(existingFilm.getId(), newFilm);
        }
        return newFilm;
    }

    // удаление фильма
    @Override
    public boolean delete(Long filmId) {
        log.info("Удаление фильма из хранилища с id: {}", filmId);
        boolean result = delete(filmId);
        if (!result) {
            log.warn("Не удалось удалить фильм из хранилища с id: {}", filmId);
        }
        return result;
    }

    // поиск фильма по id
    @Override
    public Optional<Film> getFilmById(Long id) {
        log.info("Поиск фильма в хранилище по id: {}", id);
        return Optional.ofNullable(films.get(id));
    }

    private boolean checkContainsFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Ошибка: фильм с id = {} в хранилище не найден", film.getId());
            throw new NotFoundException("Фильм с id = " + film.getId() + " в хранилище не найден");
        }
        return true;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
