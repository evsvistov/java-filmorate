package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingDaoImpl implements MpaRatingDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaRatingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> findAll() {
        String sql = "SELECT * FROM mpa order by id asc";
        return jdbcTemplate.query(sql, new MpaRatingRowMapper());
    }

    @Override
    public Optional<MpaRating> findById(Long id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        try {
            MpaRating mpaRating = jdbcTemplate.queryForObject(sql, new MpaRatingRowMapper(), id);
            return Optional.ofNullable(mpaRating);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private boolean existsMPAById(Long id) {
        String sql = "SELECT EXISTS (SELECT 1 FROM mpa WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    private static class MpaRatingRowMapper implements RowMapper<MpaRating> {
        @Override
        public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MpaRating(
                    rs.getLong("id"),
                    rs.getString("name")
            );
        }
    }
}
