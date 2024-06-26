package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {

    List<Genre> findAll();

    Optional<Genre> findById(Long id);

    void addGenreToFilm(Long filmId, Long genreId);

    void removeGenreFromFilm(Long filmId, Long genreId);

    boolean existsById(Long id);

}
