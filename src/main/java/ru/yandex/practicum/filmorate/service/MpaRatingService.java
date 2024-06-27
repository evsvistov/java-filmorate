package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления рейтингами MPA.
 */
@Service
public class MpaRatingService {

    private final MpaRatingDao mpaRatingDao;

    @Autowired
    public MpaRatingService(MpaRatingDao mpaRatingDao) {
        this.mpaRatingDao = mpaRatingDao;
    }

    public List<MpaRating> findAll() {
        return mpaRatingDao.findAll();
    }

    public Optional<MpaRating> findById(Long id) {
        return mpaRatingDao.findById(id);
    }
}
