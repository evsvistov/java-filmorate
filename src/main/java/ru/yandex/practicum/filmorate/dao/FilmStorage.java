package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(final Film film);

    Film update(final Film film);

    boolean delete(Long filmId);

    Optional<Film> getFilmById(Long id);

}
