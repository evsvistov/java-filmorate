package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {

    private final GenreDao genreDao;

    @Autowired
    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public List<Genre> findAll() {
        return genreDao.findAll();
    }

    public Optional<Genre> findById(Long id) {
        return genreDao.findById(id);
    }

    public void addGenreToFilm(Long filmId, Long genreId) {
        genreDao.addGenreToFilm(filmId, genreId);
    }

    public void removeGenreFromFilm(Long filmId, Long genreId) {
        genreDao.removeGenreFromFilm(filmId, genreId);
    }
}
