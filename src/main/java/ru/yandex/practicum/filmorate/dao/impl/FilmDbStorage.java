package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Repository
@Qualifier("FilmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, m.id as mpa_id, m.name as mpa_name " +
                "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id";
        return jdbcTemplate.query(sql, this::filmRowMapper);
    }

    @Override
    public Film create(Film film) {
        if (film.getMpaRatingId() != null && !mpaRatingExists(film.getMpaRatingId().getId())) {
            throw new MpaNotFoundException("MPA rating не найден");
        }
        if (film.getGenreIds() != null && !genresExist(film.getGenreIds())) {
            throw new GenreNotFoundException("Жанр не найден");
        }
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            if (film.getMpaRatingId() != null) {
                ps.setLong(5, film.getMpaRatingId().getId());
            } else {
                ps.setNull(5, java.sql.Types.BIGINT);
            }
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            film.setId(keyHolder.getKey().longValue());
            saveGenres(film);
            return getFilmById(film.getId()).orElseThrow(() -> new InternalServerException("Не удалось получить данные фильма после вставки"));
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    private void saveGenres(Film film) {
        if (film.getGenreIds() == null || film.getGenreIds().isEmpty()) {
            return; // Если жанры не указаны, ничего не делаем
        }

        String sqlDelete = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlDelete, film.getId());

        String sqlInsert = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenreIds()) {
            System.out.println("film.getId()=" + film.getId() + "genre.getId()=" + genre.getId());
            jdbcTemplate.update(sqlInsert, film.getId(), genre.getId());
        }
    }

    @Override
    public Film update(Film film) {
        if (!doesFilmExist(film.getId())) {
            throw new ResourceNotFoundException("Фильм с id " + film.getId() + " не существует");
        }
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpaRatingId() != null ? film.getMpaRatingId().getId() : null, film.getId());
        return film;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM films WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, m.id as mpa_id, m.name as mpa_name " +
                "FROM films f JOIN mpa m ON f.mpa_id = m.id WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::filmRowMapper, id);
        return films.stream().findFirst();
    }

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, filmId, userId);
    }

    // Удаление лайка у фильма
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    // Получение списка топ-фильмов по количеству лайков
    public List<Film> getTopFilms(int limit) {
        String sql = """
                SELECT f.*, COUNT(l.user_id) as likes_count
                FROM films f
                LEFT JOIN film_likes l ON f.id = l.film_id
                GROUP BY f.id
                ORDER BY likes_count DESC
                LIMIT ?
                """;
        return jdbcTemplate.query(sql, this::filmRowMapper, limit);
    }

    private boolean doesFilmExist(Long filmId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM films WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, filmId);
    }


    private boolean mpaRatingExists(Long mpaId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM mpa WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new Object[]{mpaId}, Boolean.class));
    }

    private boolean genresExist(Set<Genre> genres) {
        String sql = "SELECT EXISTS (SELECT 1 FROM genre WHERE id = ?)";
        for (Genre genre : genres) {
            Boolean exists = jdbcTemplate.queryForObject(sql, new Object[]{genre.getId()}, Boolean.class);
            if (Boolean.FALSE.equals(exists)) {
                return false;
            }
        }
        return true;
    }

    private Film filmRowMapper(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        Long mpaId = rs.getLong("mpa_id");
        String mpaName = rs.getString("mpa_name");

        MpaRating mpaRating = new MpaRating(mpaId, mpaName);

        List<Genre> genres = getGenresForFilm(id);
        return new Film(id, name, description, releaseDate, duration, mpaRating, new LinkedHashSet<>(genres));
    }

    private List<Genre> getGenresForFilm(Long filmId) {
        String sql = "SELECT g.id, g.name FROM film_genre fg " +
                "JOIN genre g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.id";
        return jdbcTemplate.query(sql, new Object[]{filmId}, (rs, rowNum) ->
                new Genre(rs.getLong("id"), rs.getString("name")));
    }

}

